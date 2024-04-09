package edu.kh.project.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member; //
import edu.kh.project.member.model.service.MemberService;
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
									    Model model/* 기본이 RequestScope*/ ) {
		
		
		
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
			
			
		}
		return "redirect:/"; // 메인페이지 재요청
	}
}
