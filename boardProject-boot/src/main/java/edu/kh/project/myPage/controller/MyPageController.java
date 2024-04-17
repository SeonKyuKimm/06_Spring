package edu.kh.project.myPage.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.dto.UploadFile;
import edu.kh.project.myPage.model.service.myPageService;
import lombok.RequiredArgsConstructor;

@SessionAttributes({ "loginMember" })
@Controller
@RequestMapping("myPage")
@RequiredArgsConstructor // 필드에 있는 final 예약어 애들을 내부적으로 Autowired 붙게함
public class MyPageController {

	private final myPageService service;

	/**
	 * 내 정보 조회/ 수정 화면으로 전환
	 * 
	 * @param loginMember : 세션에 존재하는 loginMember를 얻어와 매개변수에 대입함
	 * @param model       : 데이터 전달용 객체 (Request scope)
	 * @return /myPage/info로 요청 위임
	 */
	@GetMapping("info") // /myPage/info (GET)
	public String info(@SessionAttribute("loginMember") Member loginMember, Model model) {

		// 주소 부분 가공을할거다. 주소만 꺼내오기!
		String memberAddress = loginMember.getMemberAddress();

		// 주소부분은 필수가 아니어서 작성을 하지 않았다 . null 값. 주소가 있을 경우만 동작해라!

		if (memberAddress != null) {

			// 구분자 ^^^ 사용중이다. 이걸 기준으로
			// memberAddress 값을 쪼개서 String[] 형태로 반환하려고 함
			String[] arr = memberAddress.split("\\^\\^\\^");

			// 04540 ^^^ 서울 중구 남대문로 120 ^^^ 3층 E 강의장
			// —> [”04540” , “서울 중구 남대문로 120” , “3층 E 강의장”]
			model.addAttribute("postcode", arr[0]);
			model.addAttribute("address", arr[1]);
			model.addAttribute("detailAddress", arr[2]);
			// 요 상태로 myPage로 포워딩되는중~

		}

		// /templates/myPage/myPage-info.html
		return "myPage/myPage-info";
	}

	/**
	 * 프로필 이미지 변경 화면으로 이동
	 * 
	 * @return
	 */
	@GetMapping("profile")
	public String profile() {

		return "myPage/myPage-profile";
	}

	/**
	 * 비밀번호 변경 화면으로 이동
	 * 
	 * @return
	 */
	@GetMapping("changePw")
	public String changePw() {

		return "myPage/myPage-changePw";
	}

	/**
	 * 탈퇴 화면 이동
	 * 
	 * @return
	 */
	@GetMapping("secession")
	public String secession() {

		return "myPage/myPage-secession";
	}

	/**
	 * 회원 정보 수정
	 * 
	 * @param inputMember   : 제출된 회원 닉네임, 전화번호, 주소( a, b, c)
	 * @param loginMember   : 로그인한 회원 정보 (회원 정보 사용)
	 * @param memberAddress : 주소만 따로 받은 String[]
	 * @param ra            : 리다이렉트 시 request scope로 데이터 전달
	 * @return redirect:info
	 */
	@PostMapping("info")
	public String updateInfo(@ModelAttribute Member inputMember, @SessionAttribute("loginMember") Member loginMember,
			@RequestParam("memberAddress") String[] memberAddress, RedirectAttributes ra) {

		// inputMember에 현재 로그인한 회원번호를 추가하자
		int memberNo = loginMember.getMemberNo();
		inputMember.setMemberNo(memberNo);

		// 회원정보 수정 서비스 호출
		int result = service.updateInfo(inputMember, memberAddress);

		String message = null;

		if (result > 0) {

			message = "회원 정보 수정 성공!!";

			// loginMember는
			// 세션에 저장된 로그인한 회원 정보가 저장된 객체를 참조하고 있다 !!
			// 서비스 호출했을때 DB값만 바뀐거라서

			// -> loginMember 를 수정하면
			// 세션에 저장된 로그인한 회원 정보가 수정된거

			// == 세션 데이터와 DB 데이터를 맞춰줘야됨
			loginMember.setMemberNickname(inputMember.getMemberNickname());
			loginMember.setMemberTel(inputMember.getMemberTel());
			loginMember.setMemberAddress(inputMember.getMemberAddress());

		} else {
			message = "회원 정보 수정 실패ㅜ";
		}

		ra.addFlashAttribute("message", message);
		return "redirect:info";
	}

	/**
	 * 비밀번호 변경
	 * 
	 * @param paramMap    : 모든 파라미터 맵으로 저장중
	 * @param loginMember : Session에 등록된 로그인한 회원 정보
	 * @param ra          :
	 * @return
	 */
	@PostMapping("changePw")
	public String changePw(@RequestParam Map<String, Object> paramMap,
			@SessionAttribute("loginMember") Member loginMember, RedirectAttributes ra) {

		// 로그인한 회원의 번호 식별용으로 가져오기 ?
		int memberNo = loginMember.getMemberNo();

		// 현재 + 새 비밀번호 + 회원번호를 서비스로 전달.
		int result = service.changePw(paramMap, memberNo);

		String path = null;
		String message = null;

		if (result > 0) {
			path = "/myPage/info";
			message = "비밀번호가 변경되었습니다 !!";

		} else {
			path = "myPage/changePw";
			message = "현재 비밀번호가 일치하지 않습니다ㅠ";
		}

		ra.addFlashAttribute("message", message);
		return "redirect:" + path;
	}

	/**
	 * 회원 탈퇴
	 * 
	 * @param memberPw    : 입력 받은 비밀번호
	 * @param loginMember : 현재 로그인한 회원의 정보를 얻어어고있다(세션에 담겨있음)
	 * @param status      : 세션 완료 용도의 객체
	 * @SessionAttributes 로 등록된 세션을 완료시킬거임
	 * @param ra
	 * @return
	 */
	@PostMapping("secession")
	public String secession(@RequestParam("memberPw") String memberPw,
			@SessionAttribute("loginMember") Member loginMember, 
			SessionStatus status, 
			RedirectAttributes ra) {

		// 서비스 호출
		int memberNo = loginMember.getMemberNo();

		int result = service.secession(memberPw, memberNo);

		String message = null;
		String path = null;

		if (result > 0) {
			message = "탈퇴되었습니다!";
			path = "/";

			status.setComplete();// 세션을 완료시킴
		} else {
			message = "비밀번호를 확인해보세용";
			path = "secession";

		}

		ra.addFlashAttribute("message", message);

		return "redirect:" + path;

	}
	
	//-------------------------------------------------------
	/* 파일 업로딩 테스트 */
	
	@GetMapping("fileTest")
	public String fileTest() {
		
		
		return "myPage/myPage-fileTest";
	}
	
	
	/* spring에서 파일 업로드를  처리하는 방법
	 * 
	 * - enctype="multipart/form-data" 로 클라이언트 요청을 받으면
	 * 	 ( 문자, 숫자, 파일 등이 섞여을 수 있는 요청)
	 * 	 이를 MultipartResolver(FileConfig만들어야함)를 이용해서
	 *   섞여있는 파라미터를 분리해야한다.....
	 * 
	 *   문자 숫자 -> string으로 분류해줌
	 *   파일 	 -> MultipartFile 이라는 타입으로 분류해줌
	 *   
	 * 
	 */
	
	/**
	 * @param uploadFile : 
	 * 		  우리가 올린 업로드 파일 + 파일에 대한 내용 및 설정내용도 같이있다
	 * @return
	 */
	@PostMapping("file/test1")
	public String fileUpload1(
			/*@RequestParam("memberName") String memberName,*/
			@RequestParam("uploadFile") MultipartFile uploadFile,
			RedirectAttributes ra
			) throws Exception{
		
		String path = service.fileUpload1(uploadFile);
		
		// 서비스 갔다온 상황임 만약에 파일이 저장되어서
		// 웹이서 접근할 수 있는 경로 "C:/uploadFiles/test" 가 반환되었을 때
		if(path != null ) {
			ra.addFlashAttribute("path",path);
		}
		
		return "redirect:/myPage/fileTest";
	}
	
	@PostMapping("file/test2")
	public String fileUpload2(
					@RequestParam("uploadFile") MultipartFile uploadFile,
					@SessionAttribute("loginMember") Member loginMember,
					RedirectAttributes ra) throws IOException {
		
		// 로그인한 회원의 번호를 얻어와보자 !! ( 누가 업로드 했는가를 알아야해서이다 )
		int memberNo = loginMember.getMemberNo();
		
		// 업로드된 파일정보를 DB에 INSERT 한 후에, 결과 행의 개수를 반환받을것입니다.
		// 접근 : memberNO / INSERT : memberNo로 접근하여 넣기
		int result = service.fileUpload2(uploadFile, memberNo);
		
		String message = null;
		
		if(result > 0) {
			message = "파일 업로드 성공";
			
		}else {
			message ="파일 업로드 실패";
			
		}
		
		ra.addFlashAttribute("message",message);
		
		return "redirect:/myPage/fileTest";
	}
	
	/**업로드 파일 목록 조회
	 *  @param model
	 *  @return
	 */
	@GetMapping("fileList")
	public String fileList(Model model) {
		
		// 파일 목록 조회 서비스 호출
		List<UploadFile> list = service.fileList();
		
		// model list 담아서
		model.addAttribute("list", list);
		
		// myPage/myPage-fileList.html
		// forward 접두사 접미사 뺴고 ~
		return "myPage/myPage-fileList";
		
	}
	
	@PostMapping("file/test3")
	public String fileUpload3(// name속성값이 같은애들은 List로 받아라
				@RequestParam("aaa") List<MultipartFile> aaaList,
				@RequestParam("bbb") List<MultipartFile> bbbList,
				@SessionAttribute("loginMember") Member loginMember,
				RedirectAttributes ra
			)throws Exception {
		
		// aaa 파일 미제출 시
		// -> 0번 , 1번 인덱스 파일이 모두 비어있다
		
		int memberNo = loginMember.getMemberNo();
		
		// result == 업로드한 파일 갯수가 나오게 할거
		int result = service.fileUpload3(aaaList, bbbList, memberNo);
		
		String message = null;
		if(result == 0) {
			message ="업로드된 파일이 없습니다";
			
		}else {
			message = result + "개의 파일이 업로드 되었습니다";
		}
		
		ra.addFlashAttribute("message",message);
		return "redirect:/myPage/fileTest";
	}
	
	
	/** 프로필 이미지 변경하는 컨트롤러
	 * @param profileImg
	 * @param loginMember
	 * @param ra
	 * @return 
	 */
	@PostMapping("profile")
	public String profile(
			@RequestParam("profileImg") MultipartFile profileImg,
			@SessionAttribute("loginMember") Member loginMember,
			RedirectAttributes ra
			) throws IOException {
		
		// 서비스 호출하면 됨ㅎ
		// myPage/profile/변경된 파일명.확장자 형태의 문자열을
		// 현재 로그인한 회원의 PROFILE_IMG 값으로 수정할것(UPDATE)
		int result = service.profile(profileImg,loginMember);
		
		String message = null;
		
		if(result > 0) message = "변경 성공 ! !";
		else		   message = "변경 실패 ㅠ ";
		
		ra.addFlashAttribute("message", message);
		return "redirect:profile"; // 리다이렉트 할건데, /myPage/profile
	}
	
	
	
	

}
