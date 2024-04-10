package edu.kh.todo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.kh.todo.model.dto.Todo;
import edu.kh.todo.model.service.TodoService;
import lombok.extern.slf4j.Slf4j;

/* @ResponseBody
  
 	- 컨트롤러의 메서드 반환값을
   HTTP 응답 본문에 직접 바인딩하는 역할임을 명시하는거
 	
 	- 컨트롤러 메서드의 반환값을
   비동기 요청했던 HTML / JS 파일 부분에
   값을 돌려보낼것이다~ 명시
   
   - 그 값 자체를 forward / redirect 로 인식 X
  
  
   @RequestBody
   - 비동기 요청 (ajax) 시 전달되는 데이터 중
   	Body 부분에 포함된 요청 데이터를
   	알맞은 JAVA객체 타입으로 바인딩하는 어노테이션입니다.
  
   - 쉽게 말해 비동기 요청시 body에 담긴 값(여기선 js의 param)을
   	알맞은 타입으로 변환해서 매개변수에 저장.
   	
   	[HttpMessageConverter]
   	Spring에서 비동기 통신 시
   	- 전달되는 데이터의 자료형
   	- 응답하는 데이터의 자료형
   	위 두가지를 알맞은 형태로 가공(변환)해주는 객체
   	
   	-응답할 때 썼던 문자열이나 숫자혈을 Text열로 바꿔주고,
   	반대로 들어왔다면 똑같이 가공해줌
   	- 문자열,숫자 <->TEXT
   	- DTO <-> JSON
   	- 마땅한게 없으면 MAP 으로도 해줌 Map<->JSON
   	
   	(참고)
   	HttpMessageConverter 가 동작하기 위해서는
   	Jackson-Data-bind 라는 라이브러리가 필요한데,
   	Spring Boot 는 모듈에 자동으로 내장되어있다. (Spring legacy에서는 직접 넣어야함)(
   	(Jackson : 자바에서 JSON을 다루는 방법을 제공하는 라이브러리)
   	
  
  */


@Slf4j // 롬복에서 지원하는 log.debug 를 사용하게 해주는 어노ㅌㅔ이션
@RequestMapping("ajax") // get, post를 가리지 않고, ajax 라고 시작하는 모든 주소를 캐치해서 
					//나머지 주소는 밑의 메서드에서 일치하는데 매핑해줌
@Controller // 요청, 응답을 제어하는 역할임을 명시 + Bean에게 권한 위임하는 것을 등록
public class AjaxController {

	
	// @Autowired
	// - 등록된 Bean중 같은 타입 또는 상속관계인 Bean을
	// 	 해당 필드에 의존성 주입(DI)
	@Autowired
	private TodoService service;
	
	@GetMapping("main") // /ajax/main 의 a태그로 온 GET 요청이 매핑된것
	public String ajaxMain() {
		
		
		// 접두사 : thymeleaf에서 정해준 classpath:templetes/
		// 접미사 : 확장자, 즉 .html
		return "ajax/main";
		// templates/ajax 여야 해요. 지금은 resources 밑에 ajax 있음.
	}
	
	// 전체 Todo 개수를 조회하는~
	// Spring 컨트롤러 메서드의 return자리는 응답 페이지쪽을 보여주는 forward 또는 redirect를 하는 자리다.
	// 비동기 요청이 왔을때에는 값 자체를 호출하는 쪽으로 되돌려보내겠다 ~ 라는 표시가 필요하다. (@annotation)
	@ResponseBody // 지금 내가 얻어온 값 그대로 호출한놈에게 돌려보내겠다는 표시다 !!
	@GetMapping("totalCount")
	public int getTotalCount() {
		
		// 전체 할 일 개수 조회 서비스를 호출 및 응답받기
		int totalCount = service.getTotalCount();
		
		return totalCount;
		
	}
	
	
	@ResponseBody // 지금 내가 얻어온 값 그대로 호출한놈에게 돌려보내겠다는 표시
	@GetMapping("completeCount")
	public int getCompleteCount() {
				
		return service.getCompleteCount();
	}
	
	
	@ResponseBody // 비동기 요청 결과로 값 자체를 반환
	@PostMapping("add")
	public int addTodo(
			// JSON이 파라미터로 전달된 경우, 아래 방법으로는 얻어오기 불가능
			// @RequestParam("todoTitle") String todoTitle,    ┐
			// @RequestParam("todoContent") String todoContent ┘ 안됨
			
			@RequestBody Todo todo // 요청하는 fetch의 body에 담긴 값( body : JSON.stringify(param) )을 Todo에 저장
			) {
		log.debug(todo.toString());
		
		return service.addTodo(todo.getTodoTitle(),todo.getTodoContent() );
	}
	
	
	@ResponseBody // 지금 내가 얻어온 값 그대로 호출한놈에게 돌려보내겠다는 표시
	@GetMapping("selectList")
	public List<Todo> selectList() {

		List<Todo> todoList = service.selectList();

		return todoList;
		
		// List(Java의 전용 타입임)를 반환
		// -> JS가 인식할 수 없기 때문에
		// HttpMessageConverter가
		// JSON(JAVA,JAVASCRIPT모두 사용 된다 ㅎ) 으로 형태 변환해서 반환해줌
		// -> [{}, {}, {}] JSONArray
	}
	
	@ResponseBody // 지금 내가 얻어온 값 그대로 호출한놈에게 돌려보내겠다는 표시
	@GetMapping("detail")
	public Todo selectTodo(@RequestParam("todoNo") int todoNo) {
				
		// return 에 작성한 자료형 : Todo
		// -> HttpMessageConverter가 String 형태(JSON)로 변환해서 반환해줄거다~
		return service.todoDetail(todoNo);
		
	}
	
	// Delete 방식 요청 처리 ( 비동기 요청만 가능 !! )
	@ResponseBody // 지금 내가 얻어온 값 그대로 호출한놈에게 돌려보내겠다는 표시
	@DeleteMapping("delete")
	public int todoDelete(@RequestBody int todoNo) {
		// REST API : 자원 중심. 모든 자원을 보유한 URI
		// 활용할 수 있는 http 메서드를 이용해서 - GET 자원조회 / - POST 자원 생성
		//	자원에 대한 CRUD ( 언어간의 사용을 수월하게 ...)      - PUT 자원 업뎃 / - DELETE : 자원 삭제
		// 요청 주소 자체도 add ,delete 등으로 하는걸 restful 하다고 함;
		
		return service.todoDelete(todoNo);
	}
	
//	// 모달 내 완료 여부 변경버튼 클릭 시 -
//	@ResponseBody
//	@PostMapping("changeComplete")
//	public int changeComplete(@RequestBody Todo todo) {
//		//log.debug("todoNo={}",todo.getTodoNo());
//		//log.debug(todo.getComplete());
//		
//		int result = service.changeComplete(todo);
//		
//		return result;
//		
//	}
	
	
	// 모달 내 완료 여부 변경버튼 클릭 시 -( 수업)
	@ResponseBody
	@PutMapping("changeComplete")
	public int changeComplete(@RequestBody Todo todo ) {
		
		return service.changeComplete(todo);
		
	} 
	
	//할 일 수정
	@ResponseBody
	@PutMapping("update")
	public int todoUpdate(@RequestBody Todo todo) {
		log.debug("배고파" + todo.toString());
		return service.todoUpdate(todo);
	}
	
	
}
