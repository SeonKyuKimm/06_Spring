package edu.kh.project.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/* @Configuration
  - 설정용 클래스임을 명시
  + 객체로 생성해서 내부 코드를 서버 실행시에 모두 수행해줌
  
  
   @Bean : 개발자가 수동으로 생성한 객체의 관리를 
   		   스프링에게 넘기는 어노테이션 ( Bean으로 등록해서 관리 위임)
  
 */


@Configuration
public class SecurityConfig {

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		
		return new BCryptPasswordEncoder();
	}
}
