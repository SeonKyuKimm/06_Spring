package edu.kh.todo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.kh.todo.model.dto.Todo;
import edu.kh.todo.model.service.TodoService;
import lombok.extern.slf4j.Slf4j;

@Slf4j // Lombok에서 지원해주는 로그 객체를 자동 생성해주는 어노테이션
@Controller // 요청 및 응답을 제어하는 역할 명시 + 스프링에게 Bean으로 (개발자가 아닌 Spring에게 관리 권한을 넘기는) 등록 요청
public class MainController {

	@Autowired // DI라는 개념이 적용됨 (의존성 주입).todoservice를 상속받은 todoserviceImpl객체까지 사용 
	private TodoService service;
	
	@RequestMapping("/") // 메인페이지 ( "/" ) 로 이동하는 요청
	public String mainPage(Model model) {
		
		// 의존성 주입(DI된것) 확인하기 (log창을 통해 진짜 서비스 객체 들어오는것확인함)
		log.debug("service : " + service);
		
		// Service 메서드를 호출 한 후 결과 반환받기
		Map<String, Object> map = service.selectAll();
		
		// map에 담긴 내용 추출하기
		List<Todo> todoList = (List<Todo>)map.get("todoList");
		int completeCount = (int)map.get("completeCount");
		
		// Model : 값 전달용 객체 (request Scpoe) + session 변환 가능
		model.addAttribute("todoList", todoList);
		model.addAttribute("completeCount", completeCount);
		
		
		
		// 이제 DAO 대신에 Mapper 라는 클래스를 사용하게 될 것!!!!!! controller <-> service <-> mapper
		// 접두사 : classpath:templates/ 폴더
		// common/main
		// 접미사 : 확장자 .html
		// -> 이쪽으로 forwrad 하겠다 라는 뜻 -
		return "common/main";
	}
}
