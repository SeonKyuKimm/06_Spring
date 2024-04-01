package edu.kh.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

// Bean : 스프링이 만들고 관리하는 객체를 Bean이라고 한다.

@Controller // 요청 / 응답 제어 역할 명시 + Bean 등록
@RequestMapping("param") // /param으로 시작하는 요청을 현재 컨트롤러로 매핑
@Slf4j // log 를 이용한 메세지 출력 할 때 사용합니다 ~ ~ ~ (lombok 제공)
public class ParameterController {

	@GetMapping("main") // /param/main GET 방식 요청 매핑
	public String paramMain() {
		
		// classpath : src/main/resources
		// 접두사 : classpath/templates/
		// 접미사 : .html
		// -> src/main/resources/templates/param/param-main.html
		return "param/param-main";
	}
	
	
	/* 1 . HttpServletRequest.getParameter("key") 이용
	 	
	 	HttpServletRequest : 
	 	- 요청 클라이언트 정보, 제출된 파라미터 등을 저장한 객체
	 	- 클라이언트에서 요청이 왔을 때 생성됨
	 	
	 	
	  ArgumentResolver(전달 인자 해결사)
	  - Spring 의 Controller 메서드 작성 시
	  매개변수 자리에 원하는 객체를 작성하면
	  존재하는 객체를 바인딩, 또는 없으면 내부적으로 생성해서 바인딩을 해줌
	  
	  
	 */
	
	
	@PostMapping("test1") // /param/test1 이라는 POST 방식 요청이 왔을 때 매핑해줌
	public String paramTest1(HttpServletRequest req) {
		
		String inputName = req.getParameter("inputName");
		String inputAddress = req.getParameter("inputAddress");
		int inputAge = Integer.parseInt(req.getParameter("inputAge"));
		
		// debug : 코드 오류를 해결하는 과정 
		// -> 코드 오류는 없는데 정상수행 안될 때.. 
		// -> 값이 잘못된 경우 (보통은..) -> 값 추적
		log.debug("inputName : " + inputName);;
		log.debug("inputAddress : " + inputAddress);
		log.debug("inputAge : " + inputAge);
		/* Spring 에서 Redirect 하는방법 (재요청) 
		  
		  - Controller 메서드 반환값에 
		  	"redirect:요청주소"; 작성 ~
		  
		 */
		
		return "redirect:/param/main";
	}
	
	/* 2 . @RequestParam 어노테이션을 이용 - 낱개 파라미터 얻어오기
	 	
	 	- request 객체를 이용한 파라미터 전달 어노테이션
	 	- 매개변수 앞에 해당 어노테이션을 작성하면, 매개변수에 값이 주입된다.
	 	- 주입되는 데이터는 매개변수의 타입에 맞게 형변환 / 파싱이 자동으로 수행된다.
	 	
	  	[기본 작성법]
	  	@RequestParam("key") 자료형 매개변수명
	  	
	  	[속성 추가 작성법]
	  	@RequestParam(Value="name", required="false", defaultValue="1")
	  	
	  	value : 전달 받은 input태그의 name 속성값
	  	
	  	required : 입력된 name 속성값 파라미터 필수 여부 지정(기본값이 true)
	  	-> required : true 인 파라미터가 존재하지 않는다면 400 bad Request 에러 발생
	  	
	  	defaultValue : 기본값. 파라미터 중 일치하는 name 속성값이 없을 경우에 대입할 값 지정.
	  	-> required=false 인 경우에 사용하게 됨
	  	
	 */
	
	@PostMapping("test2")	
	public String paramTest2(@RequestParam("title")String title, 
							 @RequestParam("writer") String writer,
							 @RequestParam("price") int price,
							 @RequestParam(value="publisher", required=false, defaultValue="ABC출판사") String Publisher
							 ) {
		log.debug("title : " + title);
		log.debug("writer : " + writer);
		log.debug("price : " + price);
		log.debug("publisher : " + Publisher);
				
		return "redirect:/param/main";
	}
	
	
}
