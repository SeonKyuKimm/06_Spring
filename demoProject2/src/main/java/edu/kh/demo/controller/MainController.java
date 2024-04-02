package edu.kh.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

// Controller : 요청에 따라 알맞은 서비스 호출할지를 제어하는 클래스
//				+ 서비스 결과에 따라 어떤 응답을 할지 제어

@Controller // IOC (제어의 역전) 요청 / 응답 제어역할인 컨트롤러임을 명시하면서 + Spring 이 Bean으로 등록해서 관리함
public class MainController {
	
	// "/" 주소 요청 시 해당 메서드와 매핑 
	// - (메인페이지 지정시에는 "/" 작성 가능
	@RequestMapping("/")
	public String mainPage() {
		
		
		// forward : 요청 위임하는 것( ex. getRequestDispatchar? )
		// thymeleaf : Spring Boot에서 사용하는 템플릿 엔진
		
		// thymeleaf를 이용한 html 파일로 forward할 때
		// 사용되는 접두사와 접미사가 존재합니당
		
		// 접두사 : classpath(src/main/resources):/templates/ 
		// 접미사 : .html
		// src/resources/templates/common/main.html
				
		return "common/main";
		
	}
	
	
}
