package edu.kh.project.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import edu.kh.project.websocket.handler.ChattingWebsocketHandler;
import edu.kh.project.websocket.handler.TestWebsocketHandler;
import lombok.RequiredArgsConstructor;

@Configuration // 서버 실행 시 작성된 메서드를 모두 수행하게 함
@EnableWebSocket // 웹소켓 활성화 설정
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer{

	// Bean으로 등록된 SessionHandshakeInterceptor가 주입이 됨
	private final HandshakeInterceptor handshakeinterceptor;
	
	// WebSocket 처리 동작이 작성된 객체 의존성 주입
	private final TestWebsocketHandler testWebsocketHandler;
	
	private final ChattingWebsocketHandler chattingWebsocketHandler;
	
	@Override //WebSocketHandler 를 등록하는 메서드
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		
		//addHandler(웹소켓 핸들러, 웹소캣 요청주소 써주면 됨)
		
		registry
		.addHandler(testWebsocketHandler, "/testSock")
		// ws://localhost/testSock 으로 클라이언트가 요청을 하면
		// testWebsocketHandler가 요청을 처리하도록 등록하는것 
		.addInterceptors(handshakeinterceptor)
		// 클라이언트 연결 시 HttpSession을 가로채 핸들러에게 전달
		.setAllowedOriginPatterns("http://localhost/", 
							      "http://127.0.0.1", // 루프백 아이피
								  "http://192.168.50.237/") // 내 내부 도메인
		// 웹소켓 요청이 허용되는 ip/또는 도메인 지정
		.withSockJS(); // SockJS 지원
		
		
		//---------------------------------------------
		
		registry.addHandler(chattingWebsocketHandler, "/chattingSock")
		.addInterceptors(handshakeinterceptor)
		.setAllowedOriginPatterns("http://localhost/", 
			      "http://127.0.0.1", // 루프백 아이피
				  "http://192.168.50.237/")
		.withSockJS();
	}
}
