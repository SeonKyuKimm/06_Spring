package edu.kh.project.member.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.member.model.dto.Member;

@Mapper
public interface MemberMapper {

	/** 로그인 SQL실행해줄 mapper
	 * @param memberEmail
	 * @return loginMember
	 */
	Member login(String memberEmail);

	/** 이메일 서비스 중복 / 유효성 검사
	 * @param memberEmail
	 * @return count
	 */
	int checkEmail(String memberEmail);

}
