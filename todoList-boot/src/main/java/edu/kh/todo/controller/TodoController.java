package edu.kh.todo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.todo.model.service.TodoService;
import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.proxy.annotation.GetProxy;


@Controller
@RequestMapping("todo") // "/todo로 시작하는 모든 요청 매핑"
public class TodoController {

	@Autowired // 같은 타입 혹은 상속관계인 Bean을 의존성 주입 (DI)하는 어노테이션이다.
	private TodoService service;
	
	@PostMapping("add") // "todo/add" Post 방식 요청 매핑
	public String addTodo(
			@RequestParam("todoTitle") String todoTitle,
			@RequestParam("todoContent") String todoContent,
			RedirectAttributes ra
			) {
		
		// RedirectAttributes : 리다이렉트시 값을 1회성으로 전달하는 객체
		// RedirectAttributes.addFlashAttribute("key",value) 형식으로 잠깐 세션에 속성을 추가해준다
		
		// [원리]
		// 응답 전 : request Scope이나,
		// redirect 중 : session scope로 이동함
		// 응답 후 : (return redirect를 마친 후) request scope로 복귀함
		
		// 서비스 메서드 호출 후 결과 반환 받기 (행으로 반환됨)
		int result = service.addTodo(todoTitle, todoContent);

		// 삽입 결과에 따라 message값을 지정해보기
		String message = null;
		
		if(result > 0) message = "할 일 추가!";
		else 		   message = "할 일 추가 실패 ㅠㅠ";
		
		// 리다이렉트 후 1회성으로 사용할 데이터를 속성으로 추가해보자 ! RedirectAttributes.addAttribute
		ra.addFlashAttribute("message", message); // 리다이렉트 할 때 쓰는 객체
		
		return "redirect:/"; // 재요청, 
	}
	
	
	
	
	
	
	
	
	
	
}
