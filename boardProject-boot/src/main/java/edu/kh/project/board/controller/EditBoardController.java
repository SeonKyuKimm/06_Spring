package edu.kh.project.board.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.service.BoardService;
import edu.kh.project.board.model.service.EditBoardService;
import edu.kh.project.member.model.dto.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.proxy.annotation.Post;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("editBoard")
public class EditBoardController {

	private final EditBoardService service;
	private final BoardService boardService;
	
	/** 게시글 작성하는 화면으로 전환하는 메서드
	 * @param boardCode
	 * @return board/boardWrite
	 */
	@GetMapping("{boardCode:[0-9]+}/insert")
	public String boardInsert(
			@PathVariable("boardCode") int boardCode ) {
		
		
		return "board/boardWrite";
		// templates/board/boardWrite.html 로 forward 중
	}
	
	
	/** 게시글 작성 
	 * @param boardCode : 어떤 게시판에 작성할 글인지 구분
	 * @param inputBoard : 입력된 값 ( 제목, 내용 )이 세팅되어있음 ( 커맨드 객체라고 부름 )
	 * @param loginMember : Login한 회원의 번호를 얻어오는 용도
	 * @param images : 제출된 file 타입의 input 태그 데이터들 (이미지파일..)
	 * @param ra : 리다이렉트 시 request scope로 데이터 전달
	 * @return 
	 */
	@PostMapping("{boardCode:[0-9]+}/insert")
	public String boardInsert(
			@PathVariable("boardCode") int boardCode,
			@ModelAttribute Board inputBoard,
			@SessionAttribute("loginMember") Member loginMember,
			@RequestParam("images") List<MultipartFile> images, // 이미지를 제출하지 않더라도 5칸의 리스트는 생성이 되어있음
			RedirectAttributes ra ) throws IllegalStateException, IOException {
		/*
		 * List<MultipartFile> images
		 * 5개 모두 업로드 했다면 => 0-4번 인덱스에 파일 저장된다
		 * 5개 모두 업로드 x 라면 => 0-4번 인덱스에 파일 저장 ㄴㄴ
		 * 
		 * 2번 인덱스만 업로드 => 2번 인덱스만 파일이 저장, 0 1 3 4 번 인덱스는 저장 X
		 * 
		 * [ 로직처리 문제점 ] 
		 *  - 파일이 선택되지 않은 input 태그도 제출이 되고있다.
		 * 		(제출은 되고있는데 데이터는 ""; 빈칸임)
		 * 
		 *    -> 파일 선택이 되지 않은 (빈) input 태그 값을 서버에 저장하려고 하면 
		 *    	 오류가 발생할것입니다.
		 * [해결방법]		
		 *  - 무작정 서버에 저장하지 말고
		 *    -> 제출된 파일이 있는지 확인하는 로직을 추가로 구성해주어야 한다.
		 *    
		 *      + List 요소의 index번호 == IMG_ORDER 와 같다.
		 * */
		
		
		// 1 . boardCode와 로그인한 회원 번호를 inputBoard에 세팅해줍시다.
		inputBoard.setBoardCode( boardCode );
		inputBoard.setMemberNo( loginMember.getMemberNo() );
		
		// 2 . inputBoard를 가지고 서비스 메서드 호출 후 결과 반환받기
		// -> 성공 시 [상세 조회] 를 요청할 수 있도록
		   // 삽입된 번호를 바로 반환받자
		int boardNo = service.boardInsert(inputBoard,images);
		
		// 3 . 서비스 결과에 따라 message, Redirect 경로 지정
		String path = null;
		String message = null;
		
		if (boardNo > 0) {
			path = "/board/" +boardCode + "/" + boardNo; // 상세 조회
			message ="게시글이 작성되었습니다";
		}else {
			
			path = "insert";
			message ="게시글 작성 실패";
						
		}
		
		ra.addFlashAttribute("message",message);
		
		return "redirect:" + path;
	}
	
	
	/** 게시글 수정화면으로 전환하는 메서드
	 * @param boardCode   : 게시판 종류
	 * @param boardNo 	  : 게시글 번호
	 * @param loginMember : 로그인한 회원이 작성한 글이 맞는지 검사하는 용도
	 * @param Model 	  : forward 시   request scope로 값 전달하는 용도
	 * @param boardNo	  : redirect: 시 request scope로 값 전달하는 용도
	 * @return
	 */
	@GetMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}/update")// 정규표현식으로 모든 숫자 받음 ?cp=1 은 parameter로 받는거임
	public String boardUpdate(
					@PathVariable("boardCode") int boardCode,
					@PathVariable("boardNo") int boardNo,
					@SessionAttribute("loginMember") Member loginMember,
					Model model,
					RedirectAttributes ra
				) {
			// 수정 화면에 출력할 기존의 제목 . 내용 . 이미지 한번 더 조회
			// -> 게시글 상세 조회
		
		Map<String, Integer> map = new HashMap<>();
		
		map.put("boardCode", boardCode);
		map.put("boardNo", boardNo);
		
		// BoardService.selectOne(map) 으로 전달한다 (보드서비스의 셀렉트원 호출할 때 map 넣어서 전달한다는 뜻)
		Board board = boardService.selectOne(map);
		
		String message = null;
		String path = null;
		
		if(board == null) {
			
			message = "해당 게시글이 존재하지 않습니다";
			path = "redirect:/"; // 메인으로 보내버리기~
			
			ra.addFlashAttribute("message", message);
			
		}else if(board.getMemberNo() != loginMember.getMemberNo() ) {
			message = "본인이 작성한 글만 수정할 수 있습니다";
			
			// 해당 글을 상세조회 하는 곳으로 리다이렉트
			path = String.format("redirect:/board/%d/%d", boardCode, boardNo) ;
			
			ra.addFlashAttribute("message", message);
		}else {
			
			// 마지막은 해당 글이 존재하고, 작성자가 접근을 한 경우
			
			path = "board/boardUpdate"; // templates/board/boardUpdate.html로 forward
			model.addAttribute("board", board);
		}
		
		// board 의 값에 따라redirect 될지 forward 될지 결정됨
		return path;
	}
	
	
	/** 게시글 수정
	 * @param boardCode 	: 게시판 종류
	 * @param boardNo 		: 수정하려고 하는 게시글 번호
	 * @param inputBoard 	: 커맨드 객체 (제목, 내용이 세팅되어있음)
	 * @param loginMember	: 로그인한 회원의 번호를 이용 (로그인한 사람== 작성자)  
	 * @param images		: 제출된 타입 , input type="file" 의 모든 요소 (있어도 없어도 썸네일 포함5개 다 제출되니까)
	 * @param ra			: redirect 시 request scope 값 전달
	 * @param deleteOrder	: 삭제된 이미지 순서가 기록된 문자열 (1 , 2, 3)
	 * @param queryString	: 수정 성공 시에 이전 가지고 있던 파라미터 값 유지용 (cp=__ )
	 * @return
	 */
	@PostMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}/update")
	public String boardUpdate(
			@PathVariable("boardCode") int boardCode,
			@PathVariable("boardNo") int boardNo,
			@ModelAttribute Board inputBoard,
			@SessionAttribute("loginMember") Member loginMember,
			@RequestParam("images") List<MultipartFile> images,
			RedirectAttributes ra,
			@RequestParam(value= "deleteOrder", required=false) String deleteOrder,
			@RequestParam(value= "queryString", required=false, defaultValue="") String queryString
			) throws IllegalStateException, IOException {// 오버로딩 적용할거라 (매개변수가 달라서) 상관 없음
		
		// 1. 커맨드 객체 ( inputBoard )에는 boardCode, boardNo, memberNo에 세팅
		inputBoard.setBoardCode(boardCode);
		inputBoard.setBoardNo(boardNo);
		inputBoard.setMemberNo(loginMember.getMemberNo());
		// -> 여기까지 하면 inputBoard (제목, 내용 , boarCode, boardNo, memberNo) 가 들어가있다.
		
		// 2 . 게시글 수정 서비스 호출 후 결과 반환받기!!
		int result = service.boardUpdate(inputBoard, images, deleteOrder);
		
		
		// 3 . 서비스 결과에 따라 응답 제어
		String message = null;
		String path = null;
		
		if (result > 0) {
			message = "게시글이 수정되었습니다";
			path = String.format("/board/%d/%d%s", boardCode, boardNo, queryString);    	
							   // /board/1/2001?cp=3
		}else {
			message = "수정 실패 !";
			path = "update";// 수정 화면 전환
		}
		
		ra.addFlashAttribute("message", message);
		
		
		return "redirect:" + path;
	}
	
	
	
	
	
	
	
	
	
	
	
}
