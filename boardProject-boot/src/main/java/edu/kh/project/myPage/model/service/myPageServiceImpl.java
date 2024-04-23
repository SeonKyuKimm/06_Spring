package edu.kh.project.myPage.model.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import edu.kh.project.common.util.Utility;
import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.dto.UploadFile;
import edu.kh.project.myPage.model.mapper.MyPageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor=Exception.class) // 모든 예외 발생 시 롤백
@RequiredArgsConstructor
@PropertySource("classpath:/config.properties") // property 파일의 객체를 읽어올 때 사용한다.
public class myPageServiceImpl implements myPageService {

	private final MyPageMapper mapper;
	
	// BCrypt 암호화 객체 의존성 주입(Security config 참고해서 이해)
	private final BCryptPasswordEncoder bcrypt;
	
	// @Value 사용하여 Property 파일 가져오기
	
	@Value("${my.profile.web-path}")
	private String profileWebPath; // /myPage/profile/
	
	@Value("${my.profile.folder-path}")
	private String profileFolderPath;
	

	// 회원 정보 수정
	@Override
	public int updateInfo(Member inputMember, String[] memberAddress) {
		
		// 입력된 주소가 있을 경우
		// memberAddresss를 A^^^B^^^C 형태로 가공할거
		
		// 주소 입력 X -> inputMember.getMemberAddress() -> " , ,"
		if (inputMember.getMemberAddress().equals(",,")){
			
			// 주소에 null을 대입해주세용
			inputMember.setMemberAddress(null);
			
		}else{
			// memberAddress를 A^^^B^^^C 형태로 가공
			String address = String.join("^^^", memberAddress);
			
			inputMember.setMemberAddress(address);;
		}
		
		return mapper.updateInfo(inputMember);
	}

	// 비밀번호 수정 
	@Override // .matches() 사용
	public int changePw(Map<String, Object> paramMap, int memberNo) {
		
		// 현재 로그인한 회원의 암호화된 비밀번호를 DB에서 조회해와라!
		String originPw = mapper.selectPw(memberNo);
		
		// 현재 우리가 입력받은 평문상태의 현재 비밀번호가 paramMap안에 있고,
		// db에서 암호화된 origin 비밀번호랑 비교할거다.
		// 입력받은 현재 비밀번호와(평문상태)
		// DB에서 조회한 비밀번호 비교 (암호화상태)
		//BCryptPasswordEncoder.matchesa(평문,암호화된 비밀번호) -맞으면 true 아니면 false
		
		//다를 경우
		if( !bcrypt.matches((String)paramMap.get("currentPw"), originPw)) {
			return 0;
			// matches 함수는 true false 로 값을 반환함
		}
		
		//같을 경우,
		
		// 새 비밀번호를 암호화 진행
		String encPw = bcrypt.encode((String)paramMap.get("newPw"));
		
		paramMap.put("encPw", encPw);
		paramMap.put("memberNo", memberNo);
		
		
		
		return mapper.changePw(paramMap);
	}

	// 회원 탈퇴
	@Override
	public int secession(String memberPw, int memberNo) {
		
		// 현재 로그인한 회원의 암호화된 비밀번호를 조회
		String originPw = mapper.selectPw(memberNo);
		
		// 다를경우
		if( !bcrypt.matches(memberPw,originPw) ) {
			
			return 0;
		}
		
		// 같을 경우 (왜? 입력비번이 같으면 디비로 가서 일치하는 memberNo를 가진애의delfl을 Y로 해야징 
		return mapper.secession(memberNo);
	}

	// 파일 업로드 테스트 1
	// DB에 저장까지는 안할거다.
	// 제공하는 메서드만 몇 개 보자
	@Override
	public String fileUpload1(MultipartFile uploadFile) throws Exception {
		
		// MultipartFile이 제공하는 메서드
		// - getSize() : 파일 크기
		// - isEmpty() : 업로드한 파일이 비어있다면, true를 반환하는 메서드다
		// - getOriginalFileName() : 원본 파일 이름
		// - transferTo("경로") : 
		//		임시저장경로에서 쓸 일이있다면 원하는 진짜 경로로 전송하는 경로.
		//		메모리 또는 임시 저장 경로에 업로드 된 파일을
		// 		원하는 경로에 전송하는 일을 함(서버에 어떤 폴더에 저장할지 지정)
		
		if(uploadFile.isEmpty()) { // 업로드한 파일이 없을 경우
			
			return null;
		}
		
		// 업로드한 파일이 있을 경우
		// C:/uploadFiles/test/파일명 으로 서버에 저장할것임
		uploadFile.transferTo(
					new File("C:\\uploadFiles\\test\\" + uploadFile.getOriginalFilename())
				);
		// 실제 웹에서 해당 파일에 접근할 수 있는 경로를 반환해줄거임
		// 서버 : "C:\\uploadFiles\\test\\a.jpg
		// 클라이언트가 실제 웹에 접근할때는 : /myPage/file/a.jpg 라고하면 얻어다 줌
		// 근데 클라이언트가 저 myPage 어쩌구를 어케 앎. 나도 모르는데 ;;
		
		
		
		return "/myPage/file/" + uploadFile.getOriginalFilename();
	}

	// 파일 업로드 테스트2 (+DB)
	@Override
	public int fileUpload2(MultipartFile uploadFile, int memberNo) throws IOException {
		
		// 업로드 된 파일이 없다면~ 을 먼저 따져보자
		// == 인풋을 통해 선택된 파일이 없다
		if(uploadFile.isEmpty()) {
			return 0;
		}
		
		/* BLOB 타입. DB에 파일 저장이 가능은 하지만 
		 * DB 부하를 줄이기 위해서 파일 자체를 넣지는 않고,
		 * 
		 *  1 ) DB에는 서버에 저장할 파일의 경로를 INSERT해준다
		 *  
		 *  2 ) DB 삽입/수정 성공 후 서버에 파일을 INSERT한다
		 *  
		 *  3 ) 만약 파일 저장 실패 시,
		 *  	-> 예외발생
		 *  	-> @Transactional을 이용해서 rollback 수행해준다
		 * */
		
		// 뭔가 올릴 값이 있다면 
		// 1 . 서버에 저장할 파일 경로 만들기
		
		// 파일이 저장될 서버 폴더 경로
		String folderPath = "C:\\uploadFiles\\test\\";
		
		// 클라이언트가 위의 서버 경로를 알 방법이 없으므로
		// 파일이 저장된 폴더에 접근할 수 있는 주소
		String webPath ="/myPage/file/";
		
				/*
				얘 때문에 위 경로를 작성해서 연결이 가능하다고 보면 됨 
				 @Override
				public void addResourceHandlers(ResourceHandlerRegistry registry) {
				
				registry.addResourceHandler("/myPage/file/**") // 클라이언트의 요청 주소 패턴
				.addResourceLocations("file:///C:/uploadFiles/test/");
				// Client가 /myPage/file/**패턴으로 이미지를 요청할 때,
				// 요청을 연결해서 처리해 줄 서버 폴더 경로로 연결
				}
	 			*/
	 
	 	// 2 . DB 에 전달할 데이터를 DTO로 묶어서 INSERT 호출하기
	 	// webPath, memberNo, 원본 파일명, 변경된 파일명(의 로직을 실행할 클래스 생성할거임)
		// utility 클래스에서 만든걸로
		String fileRename = Utility.fileRename(uploadFile.getOriginalFilename());
		
		log.debug(fileRename);		
		
		// 이 시점에 DTO 하나 만들러갔음 .
//		UploadFile uf = new UploadFile();
//		// @setter이용해서 필요한거만 가져오기

//				
//		uf.setMemberNo(memberNo);
//		uf.setFilePath(webPath);
//		uf.setFileOriginalName(uploadFile.getOriginalFilename());
//		uf.setFileRename(fileRename);

		// Builder 패턴을 이용해서 UploadFile 객체를 생성해보자
		// 장점 1) 반복되는 참조변수명, set 구문 생략 가능
		// 장점 2) method chaining을 이용해서 한 줄로 작성 가능	
		UploadFile uf = UploadFile.builder()
						.memberNo(memberNo)
						.filePath(webPath)
						.fileOriginalName(uploadFile.getOriginalFilename())
						.fileRename(fileRename)
						.build();
		
		int result = mapper.insertUploadFile(uf);
		
		// 3 . 삽입 (INSERT)성공 시, 파일을 지정된 서버 폴더에 실제로 저장하기
		
		// 삽입 실패 시
		if(result == 0) return 0;
		
		// 삽입 성공 시
		
		// C:\\uploadFiles\\test\\변경된파일명(240417110405_00001.png)으로
		// 파일을 서버에 저장할거다 
		
		uploadFile.transferTo(new File(folderPath + fileRename));
					//C:\\uploadFiles\\test\\(240417110405_00001.png)
		// -> chechedException 발생 -> 예외처리 필수
		
		// @Transactional은 RuntimeException(Unchecked Exception의 대표다)만 처리한다
		// -> rollbackFor 속성 이용해서
		// 롤백할 예외 범위를 수정. @Transactional(rollbackFor=Exception.class)
		
		return result;
	}

	// 업로드 파일 목록 조회
	@Override
	public List<UploadFile> fileList() {
		
		return mapper.fileList();
	}
	
	// 여러 파일 업로드
	@Override
	public int fileUpload3(List<MultipartFile> aaaList, List<MultipartFile> bbbList, int memberNo) throws Exception {
		// 1. aaaList 처리
				int result1 = 0;
				
				// 업로드된 파일이 없을 경우를 제외하고 업로드
				for( MultipartFile file : aaaList) {
					
					if(file.isEmpty()) {
						continue;
					}
					
					// fileUpload2() 메서드를 호출할거임 (재활용)
					// -> 파일 하나 업로드 + DB INSERT까지
					result1 += fileUpload2(file, memberNo);
					
				}
				
				// 2 . bbbList 처리
				int result2 = 0;
				
				// 업로드된 파일이 없을 경우를 제외하고 업로드
				for( MultipartFile file : bbbList) {
					
					if(file.isEmpty()) {
						continue;
					}
					
					// fileUpload2() 메서드를 호출할거임 (재활용)
					// -> 파일 하나 업로드 + DB INSERT까지
					result2 += fileUpload2(file, memberNo);
						
				}
				
				
				return result1 + result2;
		
	}

	// 내 정보 변경 - 프로필 이미지 변경
	@Override
	public int profile(MultipartFile profileImg, Member loginMember) throws IOException {
		
		// 1 ) 수정할 경로 얻어오기
		String updatePath = null;
		
		// 변경명 저장
		String rename = null;
		
		// 업로드한 이미지 있는지 없는지 따져볼것
		// - 있을경우 :수정할 경로를 조합해줌 ( 클라이언트에서 접근하는 경로 + FileRename한거)
		if(!profileImg.isEmpty() ) {
			
			// String updatePath 조합해줄거다
			
			// 1 . 파일명 변경
			rename = Utility.fileRename( profileImg.getOriginalFilename() ); 
			// 20240417102705(년월일시분초)_00004.jpg
			
			// 2 . /myPage/profile/변경된파일명 -> 필드에 얻어둠
			updatePath = profileWebPath + rename; // 1번에서 만든거 붙여줌
			// DB -> MEMBER TABLE PROFILE_IMG 에 들어가게됨
						
			
		}
		
		// 수정된 프로필 이미지 경로 + 회원 번호를 저장할 DTO 객체 (여기서 만듦)
		Member mem = Member.builder()
					.memberNo(loginMember.getMemberNo())
					.profileImg(updatePath)
					.build(); // 메서드 체이닝
		
		// UPDATE 수행하면 됨
		int result = mapper.profile(mem);
		
		
		if( result > 0) { // DB에 수정이 성공했다 ( o 보다 크면 )
			
			// 프로필 이미지를 없앤 경우(NULL로 수정한 경우)를 제외
			// -> 업로드한 이미지가 있을 경우만
			if(!profileImg.isEmpty() ) {
				// 파일을 서버 지정된 폴더에 저장
				profileImg.transferTo( new File(profileFolderPath + rename) );
				
			}
			
			// 세션 회원 정보에서 프로필 이미지 경로를
			// 업데이트 한 경로로 변경해주기
			loginMember.setProfileImg(updatePath);
			
		}
		
		return result;
	}

	

	
}
