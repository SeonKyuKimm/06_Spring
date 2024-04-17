package edu.kh.project.myPage.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.dto.UploadFile;

@Mapper // 맵퍼는 반드시 인터페이스로 만들어야함. @Mapper어노테이션은 인터페이션에서만 명시가 가능하다
public interface MyPageMapper {

	/** 회원 정보 수정
	 * @param inputMember
	 * @return result
	 */
	int updateInfo(Member inputMember);

	/** 회원이 비밀번호 조회
	 * @param memberNo
	 * @return 암호화된 비밀번호
	 */
	String selectPw(int memberNo);

	
	/** 비밀번호 변경
	 * @param paramMap
	 * @return result
	 */
	int changePw(Map<String, Object> paramMap);

	
	/** 회원 탈퇴
	 * @param memberNo
	 * @return result
	 */
	int secession(int memberNo) ;

	/** 파일 정보를 DB에 삽입하는 애다
	 * @param uf
	 * @return result
	 */
	int insertUploadFile(UploadFile uf);

	/** 업로드 파일 목록 조회
	 * @return
	 */
	List<UploadFile> fileList();

	/** 프로필 이미지 변경
	 * @param mem
	 * @return
	 */
	int profile(Member mem);
	
	

}
