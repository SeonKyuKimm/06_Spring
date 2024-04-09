package edu.kh.project.member.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// DTO (Data Transfer Object)
// - 데이터 전달용 객체
// - DB에 조회된 결과 또는 SQL구문에 사용할 값을 전달하는 용도
// - 관련성 있는 데이터를 한 번에 묶어서 다룸

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder // Member라는 DTO에 세팅을 편하게 해주고 Member객체를 더 쉽게 만들어줌 
public class Member {

	private int    memberNo;
	private String memberEmail;
	private String memberPw;
	private String memberNickname;
	private String memberTel;
	private String memberAddress;
	private String profileImg;
	private String enrollDate;
	private String memberDelFl;
	private int    authority; // 10개
}
