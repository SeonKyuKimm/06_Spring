package edu.kh.project.common.interceptor;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import edu.kh.project.board.model.service.BoardService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/* Interceptor : 요청 / 응답을 가로채는 객체 ( Spring에서 지원함)
 * 
 * Client - ..Filter.. <-> Dispatcher Servlet <->..Interceptor.. <-> Controller ...  
 * 
 *  * 이용하려면 HandlerInterceptor 인터페이스를 상속 받아서 구현해야한다.
 *  
 *  - 상속받아 사용하는 메서드 3가지
 *  
 *  - preHandle ( 전처리 ) : Dispatcher Servlet -> Controller 사이를 수행
 * 
 *  - postHandle ( 후처리 ) : Controller -> Dispatcher Servlet 사이를 수행함
 *  
 *  - afterCompletion ( 뷰 완성 (forward를 작성했으면 코드해석 ) 후 ) : View Resolver -> Dispatcher Servlet 사이를 수행
 *  
 *   ** DB에 있는 board타입 리스트를 얻어오기만 할거라서 
 *   	전처리 (preHandle)에서 할 일이 많다.
 */
@Slf4j
public class BoardTypeInterceptor implements HandlerInterceptor{

	//Board Service 의존성 주입
	@Autowired
	private BoardService service;
	// 기본생성자에 대한 적절한 코드 수행이 불가해서, requiredArgsConstructor사용안함ㅎ
	
	@Override
	public boolean preHandle(HttpServletRequest request, 
							 HttpServletResponse response, 
							 Object handler) throws Exception {
		
			// application scope 를 얻어올건데,
			// application scope : 
			// - 서버 종료 시 까지 유지되는 Servlet 내장 객체
			// - 서버 내에 딱 한개만 존재함
			// 	 -> 모든 클라이언트가 공용으로 사용한다고~~ ( 여기에 보드타입리스트를 저장할거 )
		
			// application scope 객체 얻어오기~
		//servletContext 안에 있는 application. 
		ServletContext application = request.getServletContext();
		
		
		// application Scope에 "boardTypeList"가 없을 경우에만 서비스 호출해서 얻어와라~
		if(application.getAttribute("boardTypeList") == null ) {
			
			log.info("boardTypeInterceptor - preHandle (전처리) 동작 실행"); // 단순히 프린트구문 찍는거랑 같음
			// 이니까 boardTypeList 조회 서비스 호출해라
			
			
			// 일반적으로 했던 흐름과 다르게, 컨트롤러로 바로 가지 
			// 않고 Service로 바로 갈거다.( DB까지 도달, 역순으로 들어옴)
			List<Map<String, Object>> boardTypeList = service.selectBoardTypeList();
			
			// 조회 결과를 application scope에 추가해라~~
			application.setAttribute("boardTypeList", boardTypeList);
		}
		
		
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}

	@Override
	public void postHandle(HttpServletRequest request, 
			    HttpServletResponse response, 
			    Object handler,
			    ModelAndView modelAndView) throws Exception {
				// 전달용 객체와 어디로 forward할지를 정하는 / 가로채는 View : 응답을 하고 가로채는것까지
		
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
								HttpServletResponse response, 
								Object handler,
								Exception ex) throws Exception {
		
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}

	
}
