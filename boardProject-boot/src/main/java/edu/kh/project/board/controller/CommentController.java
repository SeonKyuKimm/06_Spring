package edu.kh.project.board.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;

import edu.kh.project.board.model.dto.Comment;
import edu.kh.project.board.model.service.CommentService;
import lombok.RequiredArgsConstructor;

/* @RestController (REST API 구축을 위해서 사용하는 컨트롤러
 * 
 * = @Controller ( 요청 / 응답 제어 + @Bean 등록) + 
 * 		+@ResponseBody (응답 본문으로 데이터 자체를 반환) 를 사용한 효과를 준다.
 * 
 * -> 모든 응답을 응답 본문 (Ajax)으로 반환하는 컨트롤러이다. ( 비동기 요청만을 받는 컨트롤러 )
 * 
 * */

@RestController // 컨트롤러임을 명시 + Bean으로 객체를 등록, Spring에게 관리 권한 위임
@RequestMapping("comment")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService service;
	// 받을 요청이 몽땅 fetch - 비동기요청
	// "comment" 요청이 오면 해당 컨트롤러에서 잡아서 처리함.
	// 여태까지는 비동기요청에 대한 @ResponseBody 를 매번 메서드 위에 추가했었다. 상단 주석에 작성
	
	/** 댓글 목록 조회
	 * @param boardNo
	 * @return
	 */
	@GetMapping("")
	private List<Comment> select(
			@RequestParam("boardNo") int boardNo
			) {
		
			// List -> JSON(문자열) 형태로 변환해서 응답해주는 객체
			// HttpMessageConverter가 JAVA 의  LIST형태를 돌려줌. List -> JSON 문자열 로 변환해서 응답
		
		return service.select(boardNo);
	}
	
	/** 댓글/ 답글 등록
	 * @return
	 */
	@PostMapping("")
	public int insert(@RequestBody Comment comment) {
		
		return service.insert(comment);
	}
	
	
	/** 댓글 수정
	 * @return
	 */
	@PutMapping("")
	public int update(
			@RequestBody Comment comment
			) {
		
		return service.update(comment);
	}
	@DeleteMapping("")
	public int delete(
				@RequestBody int commentNo
				
			) {
		
		return service.delete(commentNo);
	}
}
