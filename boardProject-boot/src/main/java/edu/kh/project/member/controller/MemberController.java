package edu.kh.project.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member; //
import edu.kh.project.member.model.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
