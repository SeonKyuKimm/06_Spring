package edu.kh.project.email.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.kh.project.email.model.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Controller
@RequestMapping("email")
@RequiredArgsConstructor /*/ final이 붙은 필드 혹은 NOT NULL 필드에 자동으로
						    의존성을 주입해줌
						    자동완성으로 @Autowired 생성자 방식 코드 자동완성함(내부적) */
public class EmailController {

	
	private final EmailService service;
	
	// fetch요청을 받아주는 ~
	@ResponseBody
	@PostMapping("signup")
	public int signup(@RequestBody String email) {
		
		String authKey = service.sendEmail("signup", email);
		
		return 0;
	}
	
	
	
}

/* @Autowired 를 이용한 의존성 주입 방법은 3가지가 있다.
 * 1 ) 필드에 작성
 * 2 ) Setter 에작성
 * 3 ) 생성자에 작성 ( 권장 ) 
 * 
 * Lombok 라이브러리에서 제공하는
 * @RequiredAgrsConstructor를 이용하면
 * 
 * 필드 중
 * 1 ) 초기화 되지 않은 final이 붙은 필드
 * 2 ) 초기화 되지 않은 @NotNull 이 붙은 필드
 *  
 * 이 둘에 해당하는 필드에 대한 @Autowired 생성자 구문을 자동완성
 */

// 1 ) 필드에 의존성 주입하는 방법 (사실 권장 X )
// @Autowired 의존성 주입 (DI)
// private EmailService service;

//  2 ) Setter 이용 
// 
//	private EmailService service;

// 
//@Autowired
//public void setService( EmailService service){
//	this.service = service;
//}


// 3 ) 생성자에 작성 ( 가장 권장되는 방법임)
// private EmailService service;
// private MemberService service2;

// @Autowired
// public EmailController(EmailService service, MemberService service2){
// 	this.service = service;
// 	this.service2 = service2;
// }




