package edu.kh.project.member.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member; //
import edu.kh.project.member.model.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/*
 * @SessionAttributes({"key", "key", "key" ,"key"...})
 *  - Moedel 에 추가된 속성 중,
 * 	  key 값이 일치하는 속성을 session scope로 변경
 * 
 * */


@SessionAttributes({"loginMember"})
@Slf4j
@Controller // Controller 임을 명시해주고 Bean으로 등록해줭~
@RequestMapping("member")
public class MemberController {
	
	@Autowired // (DI, 의존성 주입 : 맞는 걸 찾아서 연결해줍)
	private MemberService service;
	
	/* 로그인?
	   - 특정 사이트에 아이디 / 비밀번호 등을 입력해서
	  	 해당 정보가 있으면 조회 / 서비스 이용을 할 수 있는거 ㅎ
	  	 
	   - 로그인 한 정보를 session에 기록하여
	   	 로그아웃, 브라우져 종료 시 까지
	   	 해당 정보를 계속해서 이용할 수 있게 했다~!
	   	 
	   	 암호화, 등등 + 쿠키에 아이디 저장까지
	*/
	
	/** 로그인
	 * @param inputMember : 커맨드 객체 (@ModelAttribute 생략. 난 씀)
	 * 									(MemberEmail,memberPw가 세팅된 상태다)
	 * @param ra : 리다이렉트 시 request scope로 데이터를 전달하는 객체
	 * @param model : 데이터 전달용 객체( 기본으로 RequestScope )
	 * @return "redirect:/"
	 */
	@PostMapping("login")
	public String login(@ModelAttribute Member inputMember,
									    RedirectAttributes ra,
									    Model model,/* 기본이 RequestScope*/
									    @RequestParam(value="saveId", required = false ) String saveId,
									    HttpServletResponse resp) {
		
		
		
		// 로그인이니까 로그인 서비스 호출하는겨
		Member loginMember = service.login(inputMember);
		
		// 로그인 실패 시
		if(loginMember == null) {
			ra.addFlashAttribute("message", "아이디 또는 비밀번호가 일치하지 않습니다 ㅎ");
		}
		
		// 로그인 성공 시
		if(loginMember != null) {
			
			// Session Scope에 loginMember 추가
			model.addAttribute("loginMember", loginMember);
			// 1단계 : request scope에 실어둠
			
			// 2단계 : 클래스 위에 어노테이션 @SessionAttributes() 어노테이션 때문에
					// session scope로 이동됨
			
			// ******************************
			// 아이디 저장 (Cookie를 이용)
			
			// 쿠키 객체 생성 (K : V )형태임
			Cookie  cookie = new Cookie("saveId" , loginMember.getMemberEmail() );
			//saveId = user01@kh.or.kr
			
			// 클라이언트가 어떤 요청을 할 때, 쿠키가 첨부될지 지정해줍니다.
			
			// ex) "/" : IP또는 도메인 또는 localhost 라는 뜻
			// 			 뒤에 "/" --> 메인페이지 + 그 하위 주소 모두
			cookie.setPath("/");
			
			// 만료 기간 지정
			// 아이디저장 체크 해제를 하고나서는 아이디의 쿠키가 남지 않아야하는데 ..
			if(saveId != null)  {// 아이디 저장 체크시
				
				cookie.setMaxAge( 60* 60 * 24 * 30);
				
				
			}else { // 미체크시
				cookie.setMaxAge(0); // 0초. (클라이언트의 쿠키를 삭제)
			}
			
			// 응답 객체에 쿠키 추가 -> 클라이언트로 전달
			resp.addCookie(cookie);
			
			
		}
		return "redirect:/"; // 메인페이지 재요청
	}
	
	
	/** Logout : 세션에 저장된 로그인된 회원 정보를 없앤다.( 만료 , 무효화)
	 * @param sessionStatus : 세션을 완료(없앰) 시키는 역할의 객체입니당
	 * 		- @sessionAttributes 로 등럭된 세션을 만료시키는거
	 * 		- 서버에서 기존 세션 객체가 사라짐과 동시에 
	 * 		  새로운 세션 객체가 생성되어 클라이언트와 연결해줌 (기존의 정보는 파기) 	 
	 * 		- 로그아웃 -> 만료 -> 로그인 -> 세션 재생성 반복됨
	 * 
	 * @return
	 */
	@GetMapping("logout")
	public String logout(SessionStatus status) {
		
		status.setComplete(); // 세션을 완료시킴 ( 없앰 )
		
		return "redirect:/"; // main Page redirect
	}
	
	
	/** 회원가입 페이지로 이동하는메서드
	 * @return
	 */
	@GetMapping("signup")
	public String signupPage() {
		
		return "member/signup";
				
	}
	
	@ResponseBody // 응답 본문(요청한 fetch쪽)으로 돌려보내는거
	@GetMapping("checkEmail")
	public int checkEmail(@RequestParam("memberEmail") String memberEmail) {
		
		return service.checkEmail(memberEmail);
		
	}
	
	/** 닉네임 중복 검사
	 * @param memberNickname
	 * @return 중복1 , 아님 0
	 */
	@ResponseBody
	@GetMapping("checkNickname")
	public int checkNickname(@RequestParam("memberNickname") String memberNickname) {
		
		return service.checkNickname(memberNickname);
	}
	
	
	/** 회원 가입
	 * @param inputMember : 입력된 회원 정보 (memberEmail, memberPw, memberNickname, 
	 *					 	memberTel, (memberAddress - 따로 받아서 처리)
	 * @param memberAddress : 입력한 주소 input3개의 값을 '배열'로 전달[우편번호, 도로명/지번주소, 상세주소]
	 * @param ra : 리다이렉트 시 request scope로 데이터 전달하는 객체
	 * @return
	 */
	@PostMapping("signup")
	public String signup(@ModelAttribute Member inputMember,
						 @RequestParam("memberAddress") String[] memberAddress,
						 RedirectAttributes ra) {
			
		log.debug("{}=",memberAddress[0]);
		log.debug("{}=",memberAddress[1]);
		log.debug("{}=",memberAddress[2]);
		// 회원 가입 서비스 호출하기
		int result = service.signup(inputMember, memberAddress);
		
		String path = null;
		String message = null;
		if(result > 0) { // 성공 시
			message = inputMember.getMemberNickname() + "님의 가입을 환영합니다";
			path ="/";
			
		}else { // 실패 
			message = "회원 가입 실패..ㅠ";
			path="signup";
			
		}
			
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:" +path;
		
	}
	
	
	/* 빠른로그인 
	 * @param memberEmail
	 * @param model
	 * @param ra
	 * @param request
	 * @return
	 
	@GetMapping("fastLogin")
	   @ResponseBody
	   public int testLogin(@RequestParam("memberEmail") String memberEmail, Model model,
	            RedirectAttributes ra, HttpServletRequest request
	         ) {
	      
	      log.debug(memberEmail);
	      
	      Member findMember = service.fastLogin(memberEmail);
	      if(findMember == null) {
	         return 0;
	      }
	      HttpSession session = request.getSession();
	      session.setAttribute("loginMember", findMember);
	      return 1;
	      
	      
	     
	   } */


	@GetMapping("quickLogin")
	public String quickLogin(
			@RequestParam("memberEmail") String memberEmail,// js에서 쿼리스트링에 넣어온 파라미터
			Model model,
			RedirectAttributes ra
			) {
		
		Member loginMember = service.quickLogin(memberEmail);
		
		//service - mapper -xml 보다 결과를 먼저 예상하고 작성된 if문
		if(loginMember == null) {
			ra.addFlashAttribute("message", "해당 이메일이 존재하지 않습니다.");
			
		}else {
			model.addAttribute("loginMember", loginMember);
			
		}
		
		return "redirect:/";
	}
	
	@ResponseBody // 비동기 요청 받았고, 이 값을 그대로 다시 비동기 요청보낸쪽으로 돌릴것이다.
	@GetMapping("selectMemberList")
	public List<Member> selectMemberList() {
		
		// 리턴되고 있는 값이 (java)List
		// (Spring) HttpMessageConverter가 JSON Array(문자열)로 변경해줌
		// -> (JS)response 라는 매개변수 이름으로 => response.json() => JS객체 배열
		return service.selectMemberList();		
	}
	
	@ResponseBody
	@PutMapping("resetPw")
	public int resetPw(@RequestBody int inputNo) {
		
		return service.resetPw(inputNo);
	}
	
	
	
	
	
	
	
}
