package edu.kh.project.common.exception;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/* 스프링에서 예외처리 방법 ( 우선순위별 ) !
 
  1 . 메서드에서 직접 처리 ( try - catch처리하는 방법 , throws)
  
  2 . 컨트롤러 클래스에서 클래스 단위로 모아서 처리 ***
  	  (어노테이션 @ExceptionHandler 어노테이션을 지닌 메서드를 작성해서 )
  
  3 . *** 별도로 클래스를 만들어서 프로젝트 단위로 모아서 처리하는 방법 ***
  		(@ControllerAdvice 어노테이션을 지닌 클래스를 작성해서 사용)
    
 * */

//(@ControllerAdvice - 전역적 예외 처리


@ControllerAdvice // 이제부터 해당 프로젝트 내에서 오는 에러는 죄다 이리로 온다.
public class ExceptionController {

	//Exception을 처리해줄 애임
	// @ExceptionHandler(괄호안에는 예외 종류를 작성하세용~)
	
	// 예외 종류 : Method 별로 처리할 예외를 지정해준다
	// 			 ex ) 괄호 안에 -> SQLException.class -SQL관련 예외만 처리
    //	     			      -> IOException.class - 입출력 관련 예외만 처리
	//			 			  -> Exception.class - 모든 예외를 처리하겠다~~
	
	
	@ExceptionHandler(NoResourceFoundException.class) 
	public String notFound() {// 404 처리를 해결할 메서드
		
		return "error/404.html";
		//접두사 templates 생략 ,접미사 .html 생략된 구문
	}
	
	// 예외 처리하는 메서드에서 사용가능한 매개변수 ( Controller에서 사용하는 모든 매개변수 작성 가능 )
	
	
	// 프로젝트에서 발생하는 모든 예외를 처리하는 메서드
	@ExceptionHandler(Exception.class)
	public String allExceptionHandler(Exception e,
									  Model model) {
		
		e.printStackTrace(); // 예외에 대한 상세정보 호출
		model.addAttribute("e", e); // 발생한 에러에 대한 정보를 가지고 있는 정보를 보내는 역할
		
		return "error/500";
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
}
