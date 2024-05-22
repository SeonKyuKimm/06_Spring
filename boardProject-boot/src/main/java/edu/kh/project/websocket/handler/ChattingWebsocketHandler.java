package edu.kh.project.websocket.handler;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kh.project.chatting.model.dto.Message;
import edu.kh.project.chatting.model.service.ChattingService;
import edu.kh.project.member.model.dto.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component // bean 으로 등록
@RequiredArgsConstructor
public class ChattingWebsocketHandler extends TextWebSocketHandler{

		private Set<WebSocketSession> sessions = Collections.synchronizedSet( new HashSet<>() );
		
		private final ChattingService service;
		// Client와 연결이 완료되고, 통신할 준비가 되면 실행
		@Override
		public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		
			// 연결 요청한 클라이언트의 세션이 들어오고 있으니까, field의 세션즈에 넣자!
			sessions.add(session);
			
			log.info("{} 연결됨 : " ,session.getId());
		}
		
		// 클라이언트와 연결이 종료되면 실행된다.
		@Override
		public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		
			sessions.remove(session);
			
			log.info("{} 연결 끊김 : " ,session.getId());
		}
		
		// 클라이언트로부터 text message를 받았을 때 실행된다
		@Override
		protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		
			// TextMessage message 를 가공하는 일 - js 에서 전달받은 내용. json
			// {"senderNo" : "1" , "targetNo" : "2", "chattingNo" : "8" ,message Content : "hi"}
			
			// jackson에서 제공하는애고, 자동으로 스프링의 dependencies에서 제공함
			ObjectMapper objectMapper = new ObjectMapper(); // 객체생성
			
			// 자바스크립트에서 온 애를 가공해서 msg에 담아준다
			Message msg = objectMapper.readValue(message.getPayload(), Message.class); 
			
			// Message 객체 확인
			log.info("msg : {}", msg);
			
			
			// 클라이언트로부터 메세지를 받았다는것은 DB에 메세지를 insert를 해줘야한다는 뜻
			// DB에 현재 채팅 내용을 삽입할 서비스 호출
			int result = service.insertMessage(msg);
			
			if(result > 0) {
				
				// jajva에서 데이트 ㅇ타입 원히는 방식으로 가공하는 객체
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd hh:mm");
				msg.setSendTime(sdf.format( new Date() ) );
				
				// 전역변수로 선언된 sessions에는 접속중인 모든 회원의 세션 정보가 담겨있음
				for( WebSocketSession s : sessions ) {
					
					// 가로챈 세션 꺼내오기
					HttpSession temp = (HttpSession)s.getAttributes().get("session");
					
					
					// Login된 회원 정보중에 회원 번호를 꺼내오자
					int loginMemberNo = ( (Member)temp.getAttribute("loginMember")).getMemberNo();
					
					// 누구한테 보낼건지 따질거다
					// 로그인상태인 회원 중 targetNo가 일치하는 회원에게 메세지 전달할것
					if(loginMemberNo == msg.getTargetNo() || loginMemberNo == msg.getSenderNo()) {
						
						
						// 다시 DTO(VO) Object를 JSON으로 변환 (JS에 보내야하니까)
						String jsonData = objectMapper.writeValueAsString(msg);
						s.sendMessage(new TextMessage(jsonData));
						
						
						
					}
				}
				
			}
			
		}
	
}
