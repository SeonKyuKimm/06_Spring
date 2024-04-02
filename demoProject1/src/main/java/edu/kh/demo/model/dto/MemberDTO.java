package edu.kh.demo.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// lombok : 자주 사용하는 코드를 컴파일 시 자동완성 해주는 라이브러리 -
// -> DTO(기본생성자,매개변수생성자,getter/setter,tostring) + Log slf4j


@Getter // getter 자동완성해주는 어노테이션
@Setter // setter 자동완성해주는 어노테이션
@NoArgsConstructor // 기본생성자
@ToString // toString 오버라이딩 자동완성 
public class MemberDTO {
	
  	private String memberId;
  	private String memberPw;
  	private String memberName;
  	private int memberAge;
}
