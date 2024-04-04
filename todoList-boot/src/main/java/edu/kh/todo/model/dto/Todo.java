package edu.kh.todo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter // lombok이 제공하는 어노테이션
@Setter //lombok이 제공하는 어노테이션
@ToString //lombok이 제공하는 어노테이션
@NoArgsConstructor //lombok이 제공하는 어노테이션
@AllArgsConstructor //lombok이 제공하는 어노테이션
public class Todo {

	private int todoNo;			// 할 일 번호
	private String todoTitle;	// 할 일 제목
	private String todoContent; // 할 일 내
	private String complete;	// 할 일 완료 여부 ("Y" / "N")
	private String regDate;		// 할 일 등록한 날짜( String으로 변환해서 저장할거)

}
