package edu.kh.todo.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.todo.model.dto.Todo;

/*@ Mapper
 * Mybatis 제공하는 어노테이셙
 * - 해당 어노테이션이 작성된 인터페이스는
 * namespace에 해당 인터페이스가 작성된
 * mapper.xml과 연결되어 SQL 호출/수행/결과반환이 가능하게 해주는
 * 어노테이션.
 * 
 * - Mybatis에서 제공하는 mapper의 상속 객체가 Bean으로 등록되는거다.
 * (인터페이스는 객체화 될 수 없다. interface인 TodoMapper는
 * 마이바티스에서 제공하는 객체를 대신 상속받아 인터페이스 대신 사용하는것
 * 
 * 
 * <mapper namespace="edu.kh.todo.model.Mapper.TodoMapper"> 라는 네임스페이스를
 * 찾게되면 말하는대로 호출 수행 결과반환,  연결해준다.
 * 
 * */

@Mapper
public interface TodoMapper {

	/* Mapper의 메서드명 == mapper.xml 파일 내 태그의 ID와 동일.
	 * 메서드명과 id가 같은 태그가 서로 연결된다.
	 * 아래 selectAll은 Todo-mapper 의 select태그와 연결되어있다.
	 * */
	
	
	/** 할 일 목록 조회하는 메서드
	 * @return todoList
	 */
	List<Todo> selectAll();

	
	/** 완료된 할 일 갯수 조회
	 * @return completeCount
	 */
	int getCompleteCount();

		
	/** 할 일 추가 
	 * @param todo
	 * @return result
	 */
	int addTodo(Todo todo);


	/** 할 일 상세 조회
	 * @param todoNo
	 * @return todo
	 */
	Todo todoDetail(int todoNo);


	/** 완료 여부 수정
	 * @param todo
	 * @return
	 */
	int changeComplete(Todo todo);


	/** 할 일 수정
	 * @param todo
	 * @return result
	 */
	int todoUpdate(Todo todo);


	/** 할 일 삭제
	 * @param todoNo
	 * @return result
	 */
	int todoDelete(int todoNo);


	
}
