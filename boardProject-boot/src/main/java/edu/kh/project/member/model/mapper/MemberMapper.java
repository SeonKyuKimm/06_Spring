package edu.kh.project.member.model.mapper;

import java.util.List;
import java.util.Map;

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

	/** 닉네임 중복검사
	 * @param memberNickname
	 * @return count
	 */
	int checkNickname(String memberNickname);

	/** 회원 가입 SQL실행
	 * @param inputMember
	 * @return result
	 */
	int signup(Member inputMember);

	/** 빠른 로그인
	 * @param memberEmail
	 * @return
	 */
	Member fastLogin(String memberEmail);

	/** 회원 목록 조회 List
	 * @return memberList
	 */
	List<Member> selectMemberList();

	int resetPw(Map<String, Object> map);
	
	
	

	
	
	

}
