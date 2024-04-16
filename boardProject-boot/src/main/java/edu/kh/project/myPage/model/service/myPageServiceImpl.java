package edu.kh.project.myPage.model.service;

import java.io.File;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.mapper.MyPageMapper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor=Exception.class) // 모든 예외 발생 시 롤백
@RequiredArgsConstructor
public class myPageServiceImpl implements myPageService {

	private final MyPageMapper mapper;
	
	// BCrypt 암호화 객체 의존성 주입(Security config 참고해서 이해)
	private final BCryptPasswordEncoder bcrypt;

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
		// db에서 암호화된 비밀번호랑 비교할거다.
		// 입력받은 현재 비밀번호와(평문상태)
		// DB에서 조회한 비밀번호 비교 (암호화상태)
		//BCryptPasswordEncoder.matchesa(평문,암호화된 비밀번호)
		
		//다를 경우
		if( !bcrypt.matches((String)paramMap.get("currentPw"), originPw)) {
			return 0;
			// matches 함수는 true false 로 값을 반환함
		}
		
		//같을 경우
		// 새 비밀번호를 암호화 진행
		String encPw = bcrypt.encode((String)paramMap.get("newPw"));
		
		paramMap.put("encPw", encPw);
		paramMap.put("memberNo", memberNo);
		
	
		
		return mapper.changePw(paramMap);
	}

	// 회원 탈퇴
	@Override
	public int secession(String memberPw, int memberNo) {
			
		String originPw = mapper.selectPw(memberNo);
		
		// 다를경우
		if(!bcrypt.matches(memberPw,originPw)) {
			
			return 0;
		}
		
		
		
		// 같을 경우 
		return mapper.secession(memberNo);
	}

	// 파일 경로 테스트 1
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

	
}
