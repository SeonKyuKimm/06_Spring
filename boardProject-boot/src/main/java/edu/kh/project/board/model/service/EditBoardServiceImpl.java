package edu.kh.project.board.model.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.BoardImg;
import edu.kh.project.board.model.exception.BoardInsertException;
import edu.kh.project.board.model.mapper.EditBoardMapper;
import edu.kh.project.common.util.Utility;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@PropertySource("classpath:/config.properties") // 이 안에 있는걸 프로퍼티소스로 사용한다는 어노테이션 
public class EditBoardServiceImpl implements EditBoardService{

	private final EditBoardMapper mapper;

	// 이미지 업로드용. config.properties에 있는 값을 얻어와 필드에 저장한거임
	@Value("${my.board.web-path}")
	private String webPath; // /images/board/
	@Value("${my.board.folder-path}")
	private String folderPath;// C:/uploadFiles/board/
	
	// 게시글 작성
	@Override
	public int boardInsert(Board inputBoard, List<MultipartFile> images) throws IllegalStateException, IOException {
		
		// 1 . 게시글 부분(inputBoard)을 먼저 BOARD 테이블 INSERT하기
		// ->  Insert 결과로 작성된 게시글 번호(생성된 시퀀스 번호가 게시글 번호가 될텐데,
		//     그 번호를 반환받아올거임.
		int result = mapper.boardInsert(inputBoard );
		
		// result == INSERT 결과 ( 0 or 1 )
		
		// 삽입 실패 시
		if(result == 0 ) return 0; // Controller 에 0 전달
		
		// 삽입된 게시글의 번호를 변수로 저장할거라면 ?
		// -> mapper.xml 에서 <selectKey>태그를 이용해서 생성된 BOARD_NO (SEQ_BOARD_NO.NEXTVAL)
		// 즉 boardNo 가 inputBoard에 저장된 상태 !!! (얕은 복사 개념 이해 필수 )
		
		int boardNo = inputBoard.getBoardNo();
		
		// 2 . Upload 된 이미지가 실제로 존재할 경우
		//     업로드된 이미지만 별도로 저장하여
		//     "BOARD_IMG" 테이블에 삽입하는 코드 작성!!!
		
		// 리스트에 담겨온 애들 중, 실제 업로드된 이미지의 정보를 모아둘
		// 새로운 리스트를 생성해서 담아줄거다
		// 실제 업로드된 이미지의 정보를 모아둘 List생성
		List<BoardImg> uploadList = new ArrayList<>();
		
		// images 리스트에서 하나씩 꺼내어 선택된 파일이 있는지 검사
		for( int i = 0; i < images.size(); i++ ) {
			
			// 실제로 선택된 파일이 존재하는 경우 ?!
			if( !images.get(i).isEmpty() ) {
				
				// 원본명						    //multipartFile에서 제공해주는 메서드
				String originalName = images.get(i).getOriginalFilename();
				
				// 변경명
				String rename = Utility.fileRename(originalName);
				
				// IMG_ORDER == i (인덱스 == 순서)
				
				// 모든 값을 저장할 DTO 생성 (BoardImg - Builder 패턴 사용할거다)
				BoardImg img = BoardImg.builder()
							  .imgOriginalName(originalName)
							  .imgRename(rename)
							  .imgPath(webPath)
							  .boardNo(boardNo)
							  .imgOrder(i)
							  .uploadFile(images.get(i))
							  .build();
			
				uploadList.add(img);
			}
		}
		
		// 위에도 비어있다면 , 선택한 파일이 진짜 없을 경우
		if(uploadList.isEmpty()) {
			return boardNo;
		}
		
		// 선택한 파일이 존재할 경우,
		// -> "BOARD_IMG" 테이블에 INSERT + 서버에 파일 저장 (테이블에도 넣고 서버에도 넣어야함)
		
		// result == 실제 삽입된 행의 개수 == upLoadList.size()
		result = mapper.insertUploadList(uploadList);
		
		// 다중 insert가 성공했는지를 확인 (upload 에 저장된 값이 모두 정상 영업됐느냐 ?!
		if(result ==uploadList.size()){
			
			// 전부성공
			// 젤 처음서버에 파일 저장
			for( BoardImg img : uploadList) {
				img.getUploadFile()
					.transferTo(new File(folderPath + img.getImgRename()));
			}
			

		}else {
			// 부분적으로 삽입 실패 -> 전체 서비스 실패로 판단
			// -> 이전에 삽입된 내용 모두 rollback
			
			// -> rollback 하는 방법
			// == RuntimeException 강제 발생 (@Transactional에 의해서 ), 롤백이 될거임
			
			throw new BoardInsertException("이미지가 정상 삽입되지 않음");
			
		}
						
		return boardNo;
	}
	
	
	
	
	
	
	
	
	
}
