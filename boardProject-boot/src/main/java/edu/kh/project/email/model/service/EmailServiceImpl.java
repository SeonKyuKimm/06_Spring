package edu.kh.project.email.model.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import edu.kh.project.email.model.mapper.EmailMapper;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@Transactional // 예외 발생하면 롤백할게 ~ (기본값은 커밋)
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
	
	// EmailConfig 설정이 적용된 객체 ( 메일 보내기 기능 )
	private final JavaMailSender mailSender;
	
	// 타임리프( 탬플릿 엔진) 를 이용해서  HTML 코드 -> 자바코드 로 변환
	private final SpringTemplateEngine templateEngine;
	
	// Mapper 의존성 주입
	private final EmailMapper mapper;
	
	
	
	
	// 이메일 보내기
	// 메일 관련 기능 재사용 가능
	@Override
	public String sendEmail(String htmlName, String email) {
		
		// 6자리 난수(인증코드) 생성
		String authKey = createAuthKey();
		
		try {
			// 제목
			String subject = null;
			
			switch(htmlName) {
			
				case "signup" : 
					subject = "[boardProject] 회원 가입 인증번호 입니다."; break;
			}
			
			// 인증 메일 보내기
			
			// MimeMessage : Java에서 메일을 보내는 객체
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			
			// MimeMessageHelper : 
			// Spring에서 제공하는 메일 발송 도우미 ( 간단 + 타임리프 )
			MimeMessageHelper helper 
				= new MimeMessageHelper(mimeMessage, true, "UTF-8");
			
			// 1번 매개변수 : MimeMessage 객체
			// 2번 매개변수 : 파일 전송 사용 ? true / false 로 지정
			// 3번 매개변수 : 문자 인코딩

			helper.setTo(email); // 받는사람
			helper.setSubject(subject); // switch 구문 안쪽의 제목지정
			
			helper.setText( loadHtml(authKey, htmlName), true); 
			// HTML 보낼 수 있도록 변경할 예정임 (맨위 만들어둔 난수 키 )
			// HTML 코드 해석 여부 true (innerHtml 해석 할거냐 말거냐 <br>태그 등) 
			
			// CID ( Content - ID )를 이용, 메일에 이미지 첨부
			// ( FIle첨부와는 다름, 이메일 내용 자체에 사용할 이미지 첨부)
			// logo 추가 예정
			helper.addInline("logo", new ClassPathResource("static/images/logo.jpg"));
			// Logo 이미지를 메일 내용에 첨부하는데, 
			// 사용하고 싶으면 "Logo"라는 id를 작성해

			// 메일 보내기
			mailSender.send(mimeMessage);
			
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
		// 이메일 + 인증 번호를 "TB_AUTH_KEY" 테이블에 저장
		Map<String, String> map = new HashMap<>();
		
		map.put("authKey", authKey);
		map.put("email", email);
		
		// 1 ) 해당 이메일이 DB에 존재하는 경우가 있을 수 있기 때문에
		// 이메일 하나 당 난수 하나만 되게. 
		// UPDATE문을 먼저 진행할거임 * 수정 *
		// -> 1이 반환 == UPDATE 성공 == 이미 존재해서 인증번호 변경
		// -> 0이 반환 == UPDATE 실패 == 조건으로 쓸 이메일 존재 X, 인증번호 첫발급자
		// ----------------------> INSERT 할 수 있는 조건 성립
		
		int result = mapper.updateAuthKey(map);
		
		// 2) 1번 UPDATE실패 시 INSERT 를 시도해라!
		if(result == 0) {
			result = mapper.insertAuthKey(map);
		}
		
		// 수정 삽입 후에도 result 가 == 0 (실패)
		if(result == 0) return null;
		
		// 성공
		return authKey; // 오류 없이 완료되면 authKey를 반환해주자
	}
	
	// HTML파일을 읽어와서 String으로 변환하는 메서드~ (타임리프 적용)
	// ( 재활용 가능하게끔 만들어둔 메서드 )
	private String loadHtml(String authKey, String htmlName) {
		
		// org.thymeleaf.Context 선택 
		// 타임리프가 적용된 html 상에서 사용할 값같은것들을 세팅할 수 있는 객체 
		Context context = new Context();
		
		// 타임리프가 적용된 HTML에서 사용할 값 추가
		context.setVariable("authKey", authKey);
		
		// templates/ email 폴더에서 htmlName과 같은
		// ~.html 파일 내용을 읽어와 String으로 변환하겠다는 말 ㅎ
		return templateEngine.process("email/" + htmlName, context);
	}

	/** 인증번호 생성 (영어 대문자 + 소문자 + 숫자 6자리)
	 * @return authKey
	 */
   public String createAuthKey() {
	   String key = "";
       
	   for(int i=0 ; i< 6 ; i++) {
          
           int sel1 = (int)(Math.random() * 3); // 0:숫자 / 1,2:영어
          
           if(sel1 == 0) {
              
               int num = (int)(Math.random() * 10); // 0~9
               key += num;
              
           }else {
              
               char ch = (char)(Math.random() * 26 + 65); // A~Z
              
               int sel2 = (int)(Math.random() * 2); // 0:소문자 / 1:대문자
              
               if(sel2 == 0) {
                   ch = (char)(ch + ('a' - 'A')); // 대문자로 변경
               }
              
               key += ch;
           }
          
       }
       return key;
   }

   // 이메일, 인증번호 확인
	@Override
	public int checkAuthKey(Map<String, Object> map) {
		
		return mapper.checkAuthKey(map);
	}

	
}



/* Google SMTP 를 이용한 이메일 전송하기~
 * - SMTP ? (Simple Mail Transfer Protocol, 간단한 메일 전송 규약)
 * >>> 이메일 메세지를 보내고 받을 때 사용하는 약속 (규약, 방법)
 *
 * - Google SMTP
 *
 * Java Mail Sender -> Google SMTP -> 대상에게 이메일이 전송되는 형식
 *
 * - Java Mail Sender에 Google SMTP 이용 설정 추가
 *
 * 1 ) config.properties 에 내용 추가 (계정, 앱 비밀번호)
 * 2 ) EmailConfig.java
 *
 */
