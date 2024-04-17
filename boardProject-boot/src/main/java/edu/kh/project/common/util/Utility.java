package edu.kh.project.common.util;

import java.text.SimpleDateFormat;

// 프로그램 전체적으로 사용될 유용한 기능의 모음클래스.
// 이 안에서는 static예약어만 사용하게됨
public class Utility {

	public static int seqNum = 1; // 1 ~ 99999; 반복하도록 
	
		
	public static String fileRename(String originalFileName) {
		
		// fileRename을 어떻게 보내줄거냐면, 파일이 업로드된 시간 이용
		// 20240417102705(년월일시분초)_00004.jpg
		
		//SimpleDateFormat : 시간을 원하는 형태의 문자열로 간단히 변경해주는 애임
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		
		// new java.util.Date() : 현재 시간을 저장하고 있는 자바 객체
		String date = sdf.format( new java.util.Date() );
		// 이 시점에 20240417103205 가 생성됨
		
		String number = String.format("%05d" ,seqNum); /// 이러면 .format("0이 5개", seqNum이 숫자채워줌)
		
		seqNum++; // 1씩 증가, 필드에 저장.
		if(seqNum == 100000) seqNum = 1; // 만약 seqNum 이 10만이 되면 다시 1로 만들어라~
		
		
		// 위에서 20240417102705(년월일시분초)_00004.jpg 패턴을 만들었고,
		// 확장자를 만들어 붙여준다
		// "문자열" .substring(인덱스)
		// - 문자열을 인덱스부터 끝까지 잘라낸 결과를 반환
		
		// "문자열".lastIndexOf(".")
		// - 문자열에서 마지막 "." 의 인덱스를 반환
		
		// 확장자 전환
		String ext = 
				originalFileName.substring(originalFileName.lastIndexOf("."));
		
		// 만약, originalFileName == 뚱이.jpg 라면 문자열의 뒤에서부터 봐서 gpj. 으로 읽어
		// substring은 0 1 2 3 으로 봐서 3을 반환함.
		// 그러니까 현재 ext 에는 .jpg가 저장되어있음
		
		return date + "_" + number + ext;
		
	}
}
