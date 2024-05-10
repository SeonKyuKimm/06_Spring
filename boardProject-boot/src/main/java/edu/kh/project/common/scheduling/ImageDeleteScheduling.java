package edu.kh.project.common.scheduling;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import edu.kh.project.board.model.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component // 딱히 역할을 명시하는건 아니고, 단순히 @Bean으로 등록하는데에만 사용함
@PropertySource("classpath:/config.properties") // 이 파일을 참조해서 서버를 건드리겠다~
@RequiredArgsConstructor
public class ImageDeleteScheduling {

	// 회원 프로필 이미지 파일 저장 경로
	@Value("${my.profile.folder-path}") //  C:/uploadFiles/profile/
	private String profileFolderPath;
	
	//게시판 이미지 파일 저장 경로 
	@Value("${my.board.folder-path}") //  C:/uploadFiles/board/
	private String boardFolderPath;
	
	
	private final BoardService service;
	
	
	//@Scheduled(fixedDelay = 5000) // 이전 작업이 시작된 후 5초 (5000ms)후에 수행
	//@Scheduled(fixedRate = 5000) // 이전 작업이 끝난 후 5초 후에 수행
	// cron="초 분 시 일 월 요일 [년도]" - 요일 : 1(SUN) ~ 7(SAT)
	
	//@Scheduled(cron = "0,15,30,45 * * * * *") // 시계 초 단위가 0 ,15 , 30 , 45 초 일때 수행하겠다는 뜻
	//@Scheduled(cron = "0 0 * * * *")// 정시 마다 수행
	//@Scheduled(cron = "0 0 0 * * *")// 자정
	@Scheduled(cron = "0 0 0 1 * *") // 매달 1일마다
	//@Scheduled(cron = "0,30 * * * * *") // 시계초 단위가 0, 30초인 경우 수행 (테스트옹)
	public void scheduling() {
		
		log.info("스케쥴러 동작중-!");
		
		// DB, 서버 파일 목록 비교 후 DB에 없는
		// 서버 이미지 파일 삭제를 동작하자
		
		// 1 . 서버에 파일 목록 조회하기 (PropertySource 에서 가져온@Value("${my.profile.folder-path}") //  C:/uploadFiles/profile/)
		File memberFolder = new File(profileFolderPath);
		File boardFolder = new File(boardFolderPath);
	
		// 참조하는 폴더에 존재하는 파일 목록 얻어오기
		File[] boardArr = boardFolder.listFiles();
		File[] memberArr = memberFolder.listFiles();
		
		// 두 배열을 하나로 합침 (for문 한번마 사용하기 위해서.. 이따가 for문으로 뭘 좀 해야함)
		// imageArr 이라는 빈 배열을 boardArr 과 memberArr의 길이 만큼의 크기로 만든다
		File[] imageArr = new File[boardArr.length + memberArr.length];
		
		// 배열 내용 복사하자( 깊은 복사 방법 ) 
		// System.arrayCopy(복사할 배열, 몇번인덱스부터 복사할지 번호, 새로운 배열, 새로운 배열의 몇번부터 넣을지번호,
		//					복사를 어디까지 할건지 길이)라는걸 이용해보자
		System.arraycopy(boardArr, 0, imageArr, 0, boardArr.length);
		System.arraycopy(memberArr, 0, imageArr, boardArr.length, memberArr.length);
		
		// 배열 -> List 변환 (다루기 쉽도록)
		List<File> serverImageList = Arrays.asList(imageArr); // 얘는 준비를 해 둔거고
		
		// 2 . DB 에 있는 이미지 파일 '이름'만 모두 조회 // 얘는 죄다 가져오는애
		List<String> dbImageList = service.selectDbImageList();
		
		// 3 . 서버 , DB이미지 파일명을 비교하여 
		// 서버에만 있고, DB에 없는 파일을 서버에서 삭제
		if(!serverImageList.isEmpty()) { // 서버에 이미지가 있을 경우에만 수행한다는 말
			
			for(File serverImage : serverImageList ) {
				// File.getName() : 서버 파일 이름
				
				// List.indexOf(객체) : 
				// List에 객체가 존재하면 존재하는 index 번호를 반환
				// 존재하지 않으면 -1을 반환해주는 메서드다
				if(dbImageList.indexOf(serverImage.getName() ) == -1) {
					log.info(serverImage.getName() + " 삭제함");
					serverImage.delete(); // 파일 삭제하는 메서드
					
				}
			}
			
		}
		
		
	}
	
	
}

/*
* @Scheduled
*
* * Spring에서 제공하는 스케줄러 - 스케줄러 : 시간에 따른 특정 작업(Job)의 순서를 지정하는 방법.
*
* 설정 방법
* 1) XXXSSAPPlication.java 파일에 @EnableScheduling 어노테이션 추가
* 2) 스케쥴링 동작을 위한 클래스 작성
*
*
* @Scheduled 속성
*  - fixedDelay : 이전 작업이 끝난 시점으로 부터 고정된 시간(ms)을 설정.
*    @Scheduled(fixedDelay = 10000) // 이전 작업이 끝난 후 10초 뒤에 실행
*   
*  - fixedRate : 이전 작업이 수행되기 시작한 시점으로 부터 고정된 시간(ms)을 설정.
*    @Scheduled(fixedRate  = 10000) // 이전 작업이 시작된 후 10초 뒤에 실행
*   
*   
* * cron 속성 : UNIX계열 잡 스케쥴러 표현식으로 작성 - cron="초 분 시 일 월 요일 [년도]" - 요일 : 1(SUN) ~ 7(SAT)
* ex) 2019년 9월 16일 월요일 10시 30분 20초 cron="20 30 10 16 9 2 " // 연도 생략 가능
*
* - 특수문자
* * : 모든 수.
* - : 두 수 사이의 값. ex) 10-15 -> 10이상 15이하
* , : 특정 값 지정. ex) 3,4,7 -> 3,4,7 지정
* / : 값의 증가. ex) 0/5 -> 0부터 시작하여 5마다
* ? : 특별한 값이 없음. (월, 요일만 해당)
* L : 마지막. (월, 요일만 해당)
* @Scheduled(cron="0 * * * * *") // 모든 0초 마다 -> 매 분마다 실행 (1분 0초 2분 0초 3분 0초 . . .) 
*
*
* * 주의사항 - @Scheduled 어노테이션은 매개변수가 없는 메소드에만 적용 가능.
*
*/
