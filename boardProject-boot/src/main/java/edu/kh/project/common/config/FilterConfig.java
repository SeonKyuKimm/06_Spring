package edu.kh.project.common.config;

import java.util.Arrays;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.kh.project.common.filter.LoginFilter;
import jakarta.servlet.FilterRegistration;

/* 만들어놓은 LoginFilter 클래스가 언제 적용될지 설정할것임 */

@Configuration // 서버가 켜질 때, 해당 클래스 내 모든 메서드가 실행된다. 오타나면 서버 안켜짐
public class FilterConfig {
	
	// 반환된 객체를 Bean으로 등록 : LoginFilter 로 타입을 제한함
	@Bean					      // 어떤 필터를 할건지 임포트
	public FilterRegistrationBean<LoginFilter> loginFilter(){
		 //FilterRegistrationBean : 필터를 Bean 으로 등록하는 객체
		
		// 필터 객체를 만들자. 객체를 만들고 어떻게 굴러갈지를 밑에 작성
		FilterRegistrationBean<LoginFilter> filter
										= new FilterRegistrationBean<>();
		
		// 사용할 필터 객체를 추가하자. LoginFilter는 @Bean으로 등록한적이 없어서 세팅을 따로 해줬음
		filter.setFilter(new LoginFilter());
		
		// 필터링 할 url 만들어주자 
		//  /myPage/* : myPage로 시작하는 모든 요청에 대한 필터를 만들어보자
		String[] filteringURL = {"/myPage/*"}; // myPage로 시작하는 모든 요청이 배열 형태로 들어오게 됨
		
		// 필터가 동작할 URL을 세팅
		// Arrays.asList 사용해서 List 컬렉션 패턴으로
		// -> filteringURL 배열을 List 로 변환.
		filter.setUrlPatterns(Arrays.asList(filteringURL));
		
		// 필터 이름 지정. 필터가 여러개일수 있어서 이름 지정해줘야됨
		filter.setName("loginFilter");
		
		// 필터 순서 지정. 몇번째로 동작을 할 것인가 순서
		filter.setOrder(1);
		
		return filter; // 반환된 객체가 필터를 생성해서 Bean으로 등록해줄거다
		
			
	}
	// 만들고 나서 MainController로 간다

	
}
