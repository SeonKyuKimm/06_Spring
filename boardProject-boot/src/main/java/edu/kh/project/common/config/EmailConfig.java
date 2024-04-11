package edu.kh.project.common.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@PropertySource("classpath:/config.properties")
public class EmailConfig {
	
	// @value : properties에 작성된 내용 중 키가 일치하는 값을 얻어와 필드에 대입
	@Value("${spring.mail.username}")
	private String userName;
	
	@Value("${spring.mail.password}")
	private String password;
	
	@Bean
	public JavaMailSender javaMailSender() {
		
		// 구현체
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		
		Properties prop = new Properties(); // K : V 형식
		prop.setProperty("mail.transport.protocol", "smtp"); // 전송 프로토콜 형식
		prop.setProperty("mail.smtp.auth", "true"); // 인증을 사용하겠다~
		prop.setProperty("mail.smtp.starttls.enable", "true"); // 안전한 연결을 할지 말지
		prop.setProperty("mail.debug", "true"); // 디버그 모드 사용 o , x
		prop.setProperty("mail.smtp.ssl.trust","smtp.gmail.com"); // 신뢰할 수 있는 서버 호스트 지정
		prop.setProperty("mail.smtp.ssl.protocols","TLSv1.2"); // 버전에 관해서
		
		
		mailSender.setUsername(userName);
		mailSender.setPassword(password);
		mailSender.setHost("smtp.gmail.com"); // 서버 호스트 설정
		mailSender.setPort(587); // smtp 포트 설정
		mailSender.setDefaultEncoding("UTF-8"); // 기본 인코딩 
		mailSender.setJavaMailProperties(prop);// javaMail 의 속성이 있다면 앞서 정의해둔 setProperty들을 다 추가
		
		return mailSender;
		
		
	}
}
