package edu.kh.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Bean : 스프링이 만들고 관리하는 객체

@Controller // 요청 / 응답 제어 역할인 Controller 임을 명시 + Bean 등록
public class ExampleController {
	
	/* 요청 주소 매핑하는 방법
	 	
	 	1)RequestMapping("주소")
	  
	  	2)@GetMapping("주소") : GET 방식 요청 매핑
	  	
	  	  @PostMapping("주소") : POST 방식 요청 매핑
	  	
	  	  @PutMapping("주소") : PUT 방식 요청 매핑 //레스트 API ?
	  	   
	  	  @DeleteMapping("주소") : DELETE 방식 요청 매핑 // 레스트 API?
	  
	 */
	
	
	
	@GetMapping("example") // /example GET 방식 요청 매핑
	public String exampleMethod() {
		
		// forward 하려는 html 파일의 경로를 작성하는것.
		// 단, ViewResolver가 제공하는
		// Thymeleaf 에 접두사, 접미사는 제외하고 작성하면 된다.
		// 접두사 : classpath/tamplates/
		// 접미사 : .html
		return "example"; // src/mail/resources/templates
	}
}
