package edu.kh.project.myPage.model.service;

import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.mapper.MyPageMapper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor=Exception.class) // 모든 예외 발생 시 롤백
@RequiredArgsConstructor
public class myPageServiceImpl implements myPageService {

	private final MyPageMapper mapper;
	
	// BCrypt 암호화 객체 의존성 주입(Security config 참고해서 이해)
	private final BCryptPasswordEncoder bcrypt;

	// 회원 정보 수정
	@Override
	public int updateInfo(Member inputMember, String[] memberAddress) {
		
		// 입력된 주소가 있을 경우
		// memberAddresss를 A^^^B^^^C 형태로 가공할거
		
		// 주소 입력 X -> inputMember.getMemberAddress() -> " , ,"
		if (inputMember.getMemberAddress().equals(",,")){
			
			// 주소에 null을 대입해주세용
			inputMember.setMemberAddress(null);
			
		}else{
			// memberAddress를 A^^^B^^^C 형태로 가공
			String address = String.join("^^^", memberAddress);
			
			inputMember.setMemberAddress(address);;
		}
		
		return mapper.updateInfo(inputMember);
	}

	// 비밀번호 수정 
	@Override // .matches() 사용
	public int changePw(Map<String, Object> paramMap, int memberNo) {
		
		// 현재 로그인한 회원의 암호화된 비밀번호를 DB에서 조회해와라!
		String originPw = mapper.selectPw(memberNo);
		
		// 현재 우리가 입력받은 평문상태의 현재 비밀번호가 paramMap안에 있고,
		// db에서 암호화된 비밀번호랑 비교할거다.
		// 입력받은 현재 비밀번호와(평문상태)
		// DB에서 조회한 비밀번호 비교 (암호화상태)
		//BCryptPasswordEncoder.matchesa(평문,암호화된 비밀번호)
		
		//다를 경우
		if( !bcrypt.matches((String)paramMap.get("currentPw"), originPw)) {
			return 0;
			// matches 함수는 true false 로 값을 반환함
		}
		
		//같을 경우
		// 새 비밀번호를 암호화 진행
		String encPw = bcrypt.encode((String)paramMap.get("newPw"));
		
		paramMap.put("encPw", encPw);
		paramMap.put("memberNo", memberNo);
		
		
		
		
		
		return mapper.changePw(paramMap);
	}

	// 회원 탈퇴
	@Override
	public int secession(String memberPw, int memberNo) {
			
		String originPw = mapper.selectPw(memberNo);
		
		// 다를경우
		if(!bcrypt.matches(memberPw,originPw)) {
			return 0;
		}
		
		
		
		// 같을 경우 
		return mapper.secession(memberNo);
	}

	
}
