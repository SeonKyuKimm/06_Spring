package edu.kh.project.websocket.interceptor;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpSession;

/** SessionHandshakeInterceptor 클래스
 * 
 * WebSocketHandler가 동작하기 전 / 후에
 * 연결된 클라이언트의 세션을 가로채는 동작을 작성할 클래스다
 */

@Component
public class SessionHandshakeInterceptor implements HandshakeInterceptor{
	
	// 핸들러 동작 전에 수행되는 메서드
	@Override
	public boolean beforeHandshake(ServerHttpRequest request,
								   ServerHttpResponse response,
								   WebSocketHandler wsHandler,
								   Map<String, Object> attributes) throws Exception {
		// ServletHttpRequest : HttpServletRequest의 부모 인터페이스
		// ServerHttpResponse response : HttpServletResponse 의 부모 인터페이스
		
		// attributes :" 해당 맵에 속성(데이터)은 
		//				다음에 동작할 Handler 객체에게 전달된다
		//				(HandshakeInterceptor -> Handler 데이터 전달하는 역할임)
		
		// request : 가 참조하는 객체가 ServletServerHttpRequest로 다운캐스팅이 가능한가 ?
		// (부모객체인 ServletHttpRequest 가.)
		if(request instanceof ServletServerHttpRequest) {
			
			// 다운캐스팅 부모 자식 관계가 맞다면 다운캐스팅 해라
			ServletServerHttpRequest servletRequest 
					= (ServletServerHttpRequest)request;
			
			// 웹소켓 동작을 요청한 클라이언트의 세션을 얻어올거다
			HttpSession session = servletRequest.getServletRequest().getSession();
			// 부모타입의 ServletHttpRequest ServletHttpResponse 가 매개변수로 주어지기떄문에
			// 자식타입의 객체에서 session을 뽑아오기 위한 로직이다.
			
			// 가로챈 세션을 Handler에 전달할 수 있게 값을 세팅 map 이용해서
			attributes.put("session", session);
		}
		
		
		return true; // 가로채기를 진행할지 말지의 여부, true로 작성해야 세션을 가로채서(interceptor)
					 // Handler에게 전달 가능
	}
	
	
	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		// TODO Auto-generated method stub
		
	}
}
