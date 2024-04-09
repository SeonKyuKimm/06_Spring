package edu.kh.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

// Spring security에서 기본으로 제공하는 로그인 페이지 이용 안하겠다~! 는 코드~ㅎ
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class BoardProjectBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoardProjectBootApplication.class, args);
	}

}
