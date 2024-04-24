package edu.kh.project.board.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.Pagination;
import edu.kh.project.board.model.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor // 같은타입, 자식타입에 의존성 주입을 위해 autowired(필드 생성자 세터)를 붙였었는데, 그걸 안하게 해줌
public class BoardServiceImpl implements BoardService{

	
	private final BoardMapper mapper;

	// 게시판 종류 조회
	@Override
	public List<Map<String, Object>> selectBoardTypeList() {
		
		return mapper.selectBoardTypeList();
	}

	// 특정 게시판의 지정된 페이지 목록 조회
	@Override
	public Map<String, Object> selectBoardList(int boardCode, int cp) {
		
		
		// 1. 지정된 게시판(boardCode)에서
		// 	  삭제되지 않은 게시글 수를 조회해온다
		int listCount = mapper.getListCount(boardCode);
		
		
		// 2 . 1번의 결과 + cp를 이용해서
		//     Pagenation 객체를 생성해야함
		// Pagenation 객체 : 게시글 목록 구성에 필요한 값을 저장하고 있는 객체
		Pagination pagination = new Pagination(cp, listCount);
		
		
		// 3 . 특정 게시판의 지정된 페이지 목록을 조회해야한다
		/*
		 * RowBounds 객체 (MyBatis 제공 객체)
		 * 
		 *  - 지정된 크기만큼 건너뛰고 (offset)
		 * 	  제한된 크기(limit) 만큼의 행을 조회하는 객체
		 * 
		 *  --> 페이징 처리가 굉장히 간단해진다.
		 * */
		int limit = pagination.getLimit();
		int offset = (cp-1) * limit;
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		/* Mapper 메서드 호출 시
		 * - 첫 번째 매개변수 -> SQL에 전달할 파라미터
		 * - 두 번째 매개변수 -> RouBounds 객체 전달
		 * 
		 * */ 
		List<Board> boardList = mapper.selectBoardList(boardCode, rowBounds);
		
		
		
		
		// 4 . 목록 조회 결과 + Pagenation 객체를 Map으로 묶음
		Map<String, Object> map = new HashMap<>();
		map.put("pagination", pagination);
		map.put("boardList", boardList);
		
		
		// 5 . 결과 반환
		return map;
	}

	// 게시글 상세조회
	@Override
	public Board selectOne(Map<String, Integer> map) {
		
		// 여러 SQL을 실행하는 방법 ?!
		// 1 . 하나의 Service 메서드에서 
		// 여러 Mapper메서드를 호출하는 방법
		
		// 2 . 수행하려는 SQL이
		//	   1) 모두 SELECT이면서
		//	   2) 먼저 조회된 결과 중 일부를 이용해서
		//		  나중에 수행되는 SQL의 조건으로 삼을 수 있을 때
		//		--> Mybatis에서 제공, <resultMap>, <collection> 태그를 이용,
		//			Mapper 메서드 1회 호출로 여러 SELECT 한번에 수행 가능
		
	
		return mapper.selectOne(map);
	}

	// 게시글 좋아요 체크 / 해제 메서드~ (의 오버라이딩)
	@Override
	public int boardLike(Map<String, Integer> map) {
		
		int result = 0;
		
		// 1 . 좋아요가 현재 체크된 상태인 경우 (map.likeCheck == 1) 일건데,
		// -> 해제해야함. 즉 BOARD_LIKE 테이블에 DELETE해야함
		if(map.get("likeCheck") == 1) {
			
			result = mapper.deleteBoardLike(map);
		}else {
		// 2 . 좋아요가 해제된 상황인 경우 (map.likeCheck == 0 )
		// BOARD_LIKE 테이블에 INSERT 해야함
			result = mapper.insertBoardLike(map);
		}
		
		// (board-mapper.xml을 거쳐온 delete insert 가 정상작동 했다면
		// 	return 에는 값이 담기게 된다)
		// 3 . 다시 이 해당 게시글의 좋아요 개수를 조회해서 반환해주자

		if(result > 0) {
			return mapper.selectLikeCount(map.get("boardNo"));
		}
		
		return -1 ;
		
	}

	@Override
	public int updateReadCount(int boardNo) {
		
		// 1 . 조휘 수 1 증가
		int result = mapper.updateReadCount(boardNo);
		
		// 현재 조회 수 다시 조회
		if(result > 0) {
			return mapper.selectReadCount(boardNo);
		}
		    return -1; // 실패한 경우 -1 을 반환
	}
	

	
	
	
}
