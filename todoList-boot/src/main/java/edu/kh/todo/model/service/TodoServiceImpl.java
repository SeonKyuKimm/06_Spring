package edu.kh.todo.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.todo.model.dto.Todo;
import edu.kh.todo.model.mapper.TodoMapper;

//-----------------------------------
// @Transactional
// - 트랜젝션 처리를 수행하라고 지시하는 어노테이션
// ( == 선언적 트랜젝션 처리)

// - 정상 코드 수행 시 COMMIT
// - 기본값은 : Service 내부 코드 수행 중 RuntimeException 발생 시 Rollback해주는게 기본값..

// - rollbackFor 속성 : 어떤 예외가 발생했을 때 rollback 할 지 지정
//-----------------------------------

// 모든 종류의 예외 발생 시, rollback 수행하라고 알려준것
@Transactional(rollbackFor = Exception.class)
@Service // 비즈니스 로직 처리해줌 ( 데이터 가공, 트랜젝션 처리 등) 역할 명시, + Bean 객체로 등록
public class TodoServiceImpl implements TodoService {
	
	//DAO 대신 mapper 어노테이션을 사용할것임
	@Autowired //DI (의존성 주입)가 적용된거다
	private TodoMapper mapper;

	
	/* 할 일 목록 + 완료된 할 일의 개수 조회 */
	 
	@Override
	public Map<String, Object> selectAll() {
		
		// 1 . 할 일 목록 조회
		List<Todo> todoList = mapper.selectAll();
		
		// 2 . 완료된 할 일 개수 조회
		int completeCount = mapper.getCompleteCount();
		// Map 으로 묶어서 반환.
		Map<String, Object> map = new HashMap<>();
		// 서로 다른 타입의 값을 리턴하고싶을 때 Map 을 사용
		map.put("todoList", todoList);
		map.put("completeCount", completeCount);
		
		
		// 이전에는 DAO에서 해줬던 일인데 interface TodoMapper에선 못하니까.. 
		
		return map;
	}

	/* 할 일 추가 */
	@Override
	public int addTodo(String todoTitle, String todoContent) {
		
		// Connection 생성 / 반환 X 
		// 트랜젝션 제어 처리 -> @Transactional 어노테이션 추가
		
		// 마이바티스에서 SQL에 전달할 수 있는 파라미터의 개수는 
		// 오직 1개다 !
		// todoTitle, todoContent를 Todo DTO 하나로 묶어서 전달할것임
		Todo todo = new Todo();
		todo.setTodoTitle(todoTitle);
		todo.setTodoContent(todoContent);
		
		
		return mapper.addTodo(todo);
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
