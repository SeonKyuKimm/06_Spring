package edu.kh.project.myPage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// @Builder : 빌더 패턴을 이용해서 객체 생성 및 초기화를 쉽게 진행
// -> 기본 생성자가 자동으로 생성이 안됨. 꼭 따로 생성해줘야됨
// -> Mybatis 조회결과를 담을 때 기본생성자로 객체를 만들기 때문입니다


@Getter
@Setter
@NoArgsConstructor // 기본생성자를 꼮 넣으라고 한건
@AllArgsConstructor// 마이바티스가 기본생성자를 이용함
@ToString
@Builder		   
public class UploadFile {

	private int fileNo;
	private String filePath;
	private String fileOriginalName;
	private String fileRename;
	private String fileUploadDate;
	private int memberNo;
	private String memberNickname;
}
