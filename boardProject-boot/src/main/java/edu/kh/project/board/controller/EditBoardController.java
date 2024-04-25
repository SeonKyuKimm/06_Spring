package edu.kh.project.board.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
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

	public final EditBoardService service;
	
	
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
