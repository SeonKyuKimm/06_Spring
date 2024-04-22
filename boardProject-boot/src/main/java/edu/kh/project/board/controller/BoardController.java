package edu.kh.project.board.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.kh.project.board.model.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("board")
@Slf4j
@RequiredArgsConstructor
public class BoardController {

	private final BoardService service;
							   
	/** 게시글 목록 조회
	 * @param boardCode : 게시판 종류 구분 번호
	 * @param cp : 현재 조회 요청한 페이지 (없으면 1)
	 * @return
	 * 
	 *  - /board/xxx
	 *   /board 이하 1레벨 자리에 숫자로 된 요청 주소가 
	 *    작성되어 있을때만 동작하도록 함 -> 정규표현식 이용해서 
	 * 
	 * [0-9] : 한 칸에 0 ~ 9 사이 숫자만 입력 가능ㅎ
	 *  + : 하나 이상
	 *  [0-9]+ : 모든 숫자
	 */
									 // 나중에 다른 메서드 (ex. /board/insert 등..) 도 만들 수 있으니
	@GetMapping("{boardCode:[0-9]+}") // 숫자만 들어올 수 있게 해줌
	public String selectBoardList(@PathVariable("boardCode") int boardCode,
								  @RequestParam(value="cp", required=false, defaultValue="1") int cp,
								  Model model
								  ) {
		log.debug("boardCode : " + boardCode);
		
		// 조회 서비스 호출 후 결과 반환
		Map<String, Object> map = service.selectBoardList(boardCode,cp);
		
		
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("boardList", map.get("boardList"));
		
		log.debug("pagination" + map.get("pagination"));
		
		
		return "board/boardList"; // boardList.html로 forward 시킬거다 ㅎ
	}
}
