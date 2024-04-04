package edu.kh.todo.model.service;

import java.util.Map;

// interface는 Bean객체로 등록을할 수가 없다
public interface TodoService {

	/** 할 일 목록 + 완료된 할 일의 개수 조회하는 서비스
	 * @return map
	 * 
	 */
	Map<String, Object> selectAll();

	
	/** 할 일 추가하는 서비스
	 * @param todoTitle
	 * @param todoContent
	 * @return result
	 */
	int addTodo(String todoTitle, String todoContent);
	
	
}