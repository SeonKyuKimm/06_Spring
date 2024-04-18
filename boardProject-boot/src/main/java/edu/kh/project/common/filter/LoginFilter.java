package edu.kh.project.common.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/*
 * Filter : 요청 / 응답 시 걸러내거나 추가할 수 있는 객체 ( ex.미네랄 워터 필터 )
 * 
 * [필터 클래스 생성 방법]
 * 1 . 필터라는 인터페이스를 상속받자 (jakarta.servlet.Filter인터페이스 상속받기)
 * 
 * 2 . doFilter() 메서드 오버라이딩 해야한다 ( 어떤 동작을 할 것인가에 대한 적용 메서드 )
 * */



// 로그인이 되어있지 않은 경우 특정 페이지로 돌아가게 하는 Filter
public class LoginFilter implements Filter {

	
	// 필터 동작을 정의하는 메서드
	@Override
	public void doFilter(ServletRequest request, 
						 ServletResponse response, 
						 FilterChain chain)
			throws IOException, ServletException {
		
		// ServletRequest  : HttpServletRequest의 부모 타입
		// ServletResponse : HttpServletResponse의 부모 타입
		
		// HTTP 통신이 가능한 형태로 다운 캐스팅
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		
		// 세션을 구해서 세션에 로그인 멤버가 로그인을 했는지 안했는지 따져보자.
		// req에서 Session을 뽑아오자.
		HttpSession session = req.getSession();
		
		// 세션에서 로그인한 회원 정보를 얻어오자
		// 얻어왔으나, 없을 때 -> 로그인이 되어있지 않은 상태
		if(session.getAttribute("loginMember") == null) {
			
			// loginError 라는 요청을 받을 컨트롤러 만들어서 redirect (재요청) 
			// resp를 이용해서 원하는 곳으로 redirect
			resp.sendRedirect("/loginError");
			
		}else {
			
			// 로그인멤버 != null, 로그인이 되어있는경우
			
			// FilterChain
			// - 다음 필터 또는 Dispatcher Servlet과 연결된 객체
			// 필터는 여러개 만들 수 있다 로그인필터 보안필터 인코딩필터 etc...
			
			// 다음 필터로 요청 / 응답 객체를 전달해줘야된다
			// (만약 다음 필터가 없으면 dispatcherServlet으로 전달하면 된다)
			
			chain.doFilter(request, response);
		}
		
		
	}

	
	// 요렇게 해두고, Filter Config 클래스 common.config 에 만들러 감
	
	
}
