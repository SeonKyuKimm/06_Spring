package edu.kh.project.member.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.mapper.MemberMapper;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service// 비즈니스 로직 처리 역할 명시 + Bean
@Transactional(rollbackFor=Exception.class) // 해당 클래스 메서드 종료 시까지 
			   // 예외(RuntimeException)가  발생하지 않으면 commit;
			   // 예외(RuntimeException)가  발생하면 rollback;
public class MemberServiceImpl implements MemberService{

	// 등록된 bean 중에서 같은 타입 또는 상속관계인 bean을
	// 자동으로 의존성 주입( DI )
	@Autowired 
	private MemberMapper mapper;
	
	// BCrypt 암호화 객체 의존성 주입 (Security Config 참고하면 된다! )
	@Autowired
	private BCryptPasswordEncoder bcrypt;
	
	
	@Override
	public Member login(Member inputMember) {
		
		/*	memberEmail : user01@kh.or.kr
		 	memberPw  : pass01!
		 	암호화를 거치지 않은 '평문' 상태라 #{memberPw}가 가능했었다
		 	고객정보암호화! 를 안하면 내가 구속된다 ㅠ 
		 	서비스 단에서 가공할거고, 특정 코드로 암호화 해서 DB에는
		 	암호화된 정보만 저장됨ㅇㅇ*/
		// --------------------------------------
		// 암호화 테스트 ?!
		// bcrypt.encode(문자열, 평문 비밀번호) : 문자열을 암호화하여 반환해줌
		String bcryptPassword = bcrypt.encode(inputMember.getMemberPw());
		
		//log.debug("이거야 : " + bcryptPassword);
		//boolean result = bcrypt.matches(inputMember.getMemberPw(), bcryptPassword);
		//log.debug("리져-트 : " + result);
		
		// 1 . (이메일이 일치)하면서 (탈퇴하지 않은) 회원 조회
		Member loginMember = mapper.login(inputMember.getMemberEmail());
		
		// 2 . 만약에 일치하는 이메일이 없어서, 조회 결과가 null인 경우
		if( loginMember == null) return null;
		
		// 3 . 입력받은 비밀번호(inputMember.getMemberpw() 평문,pass01! )와
		//     암호화된 비밀번호 (loginMember.getMemberPw() )
		//     두 비밀번호가 일치하는지 확인 !
		
		// 일치하지 않으면
		if( !bcrypt.matches( inputMember.getMemberPw(), loginMember.getMemberPw() ) ) {
			return null;
		}
		
		// (일치하면) 로그인 결과에서 가지고 온 비밀번호를 제거해주는 작업~~
		loginMember.setMemberPw(null);
		
		
		return loginMember;
	}


	/**이메일 서비스 중복 / 유효성 검사
	 * @param memberEmail
	 * @return mapper.checkEmail();
	 */
	@Override
	public int checkEmail(String memberEmail) {
		
		return mapper.checkEmail(memberEmail);
	}


	/** 닉네임 중복검사
	 * @return count값
	 */
	@Override
	public int checkNickname(String memberNickname) {
		
		return mapper.checkNickname(memberNickname);
	}

	// 회원 가입 서비스
	@Override
	public int signup(Member inputMember, String[] memberAddress) {
		// 주소가 입력되지 않으면
		// inputMember.getMemberAddress() -> ",,"
		// memberAddress -> [,,] 
		
		// 주소가 입력된 경우
		if( !inputMember.getMemberAddress().equals(",,") ){
			
			// 구분자를 , 로 쓰지 않고 ^로
			// String.join("구분자", 배열)
			
			// 배열의 모든 요소 사이에 "구분자" 를 추가하여
			// 하나의 문자열로 만들어 반환하는 메서드
			
			// 구분자로 ^^^ 쓴 이유?! : 
			// ->주소나 상세주소에 없을 것 같은 특수문자 작성
			// -> 나중에 다시 3분할 때 구분자로 이용할 예정
			String address = String.join("^^^", memberAddress);
			
			// inputMember 주소로 합쳐진 주소를 세팅했다
			inputMember.setMemberAddress(address);
			
			
		}else { // 주소가 입력되지 않은 경우
			
			inputMember.setMemberAddress(null); // null 저장하기
		}
		
		
		// 이메일, 비밀번호, 닉네임, 전화번호, 주소
		// 비밀번호 암호화 해서 넣어야한다. 암호화 후 / inputMember에 세팅
		String enPw = bcrypt.encode(inputMember.getMemberPw() );
		inputMember.setMemberPw(enPw);
		
		// 회원가입 매퍼 메서드 호출			
		return mapper.signup(inputMember);
	}
}

/* BCrypt 암호화 (Spring Security 제공)
     
   - Bcrypt 알고리즘이 적용. ( 원본에 크랙을 검?)
   	 입력된 문자열 (비밀번호) 에 salt 를 추가한 후 암호화 하는거임
   	
  ex) A 회원이 : 1234 비번 씀   ->   $12!asdfg~
  ex) B 회원도 : 1234 비번 씀   ->   $12!gfefg~ 라는 패턴이 ..
 
  - 비밀번호 확인 방법
  	->BCryptPasswordEncoder.matches(평문 비밀번호, 암호화된 비밀번호)
  	-> 평문 비밀번호화 암호화된 비밀번호가 같은 경우, True! 아니면 False 반환해준다. 
  
  * 로구인 / 비밀번호변경 / 탈퇴 등 비밀번호 입력되는 경우
   DB에 저장된 비밀번호(암호화된) 를 조회해서
   matches() 메서드로 비교해야 한다.
  
  
  
  
  
  
  
  
  (참고.이전에는)sha 방식 암호화
  암호화 시 암호화된 내용이 같다
  ex ) A 회원 : 1234            ->    암호화 : abcd
  ex ) B 회원 : 1234            ->    암호화 : abcd
  
   - 
  
  
*/


