package edu.kh.project.common.aop;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import edu.kh.project.member.model.dto.Member;
import jakarta.mail.Session;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Aspect // AOP동작용 클래스 
@Component
@Slf4j
public class LoggingAspect {

	
	// 어떤메서드가 어떤 사용자에 의해 어떤 동작을 하는지에 관한 메서드 하나 생성해보자
	
	/** Controller 수행 전 로그 출력(클래스/메서드/ip...)
	 * @param jp
	 */
	@Before("PointcutBundle.controllerPointCut()")
	public void beforeController(JoinPoint jp) {
		
		String className = jp.getTarget().getClass().getSimpleName();
		
		String methodName = jp.getSignature().getName() + "()"; // 아까는 메서드 네임이 나오지 않아서 + "()"
		
		// 요청한 클라이언트의 HttpServletRequest 객체 얻어오자. 응답을 해 주기 위한 과정
		// 어쨌든 http 요청 객체를 얻어왔다는거임
		HttpServletRequest req = 
				( (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes() ).getRequest();
		
		
		// 클라이언트의 IP 얻어오기~
		String ip = getRemoteAddr(req);
		
		// 이렇게 하면 ip가 담겨있다
		
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("[%s.%s] 요청 / ip : %s", className, methodName, ip));
		
		// 로그인 상태인 경우
		if( req.getSession().getAttribute("loginMember") !=null) { // 위에서 구한 rep의 세션에 접속해서 loginMember 의 값이 널이 아니라면 == 접속상태라면
			
			String memberEmail =
					( (Member)req.getSession().getAttribute("loginMember") ).getMemberEmail();
			
			sb.append(String.format(", 요청 회원 : %s", memberEmail) );
			
		}
		
		log.info( sb.toString() );
	}
	
	//-----------------------------------------------
	// ProceedingJoinPoint 
	// JoinPoint 상속한 자식 객체
	// @Around 에서 사용 가능한 객체
	// proceed() 메서드를 제공해줌
	// -> proceed() 메서드 호출 전/ 후로 Before After 가 구분되어지는것이다
	
	// * 주의할 점 *
	// 1) @Around 어노테이션 사용시 반환형은 Object여야한다
	// 2) @Around 어노테이션 메서드 종료 시 proceed() 반환값을 꼮 return 해야한다.
	
	
	/** Serivce 수행 전 후로 동작하는 코드 (advice)
	 * @return
	 * @throws Throwable 
	 */
	@Around("PointcutBundle.serviceImplPointCut()")
	public Object aroundServiceImpl( ProceedingJoinPoint pjp) throws Throwable {
		
		// Throwable : 예외 처리 최상위 클래스. 알고 있던 Exception보다도 상위 클래스
		// Throwable의 자식으로 나눠지는게 Exception(예외. 개발자가 어느정도 처리 가능) 과 error(개발자가 해결할 수 없는 치명적 문제 등)
		
		// @Before 부분
		
		// 클래스명
		String className = pjp.getTarget().getClass().getSimpleName();
		
		// 메서드명
		String methodName = pjp.getSignature().getName() + "()";
		
		
		log.info("========{}.{} Service 호출======", className, methodName);
		
		// 파라미터
		log.info("Parameter : {}" , Arrays.toString( pjp.getArgs() ) );
		
		// 서비스 코드 실행 시 시간 기록
		long startMs = System.currentTimeMillis();
		
		Object obj = pjp.proceed(); // 전 , 후를 나누는 기준점
		
		// @After 부분
		long endMs = System.currentTimeMillis(); // 대상메서드의 수행시간이 끝난 다음에 또 시간을 기록함
		
		// endMs - startMs 할 수 있다
		log.info("running Time : {}ms", endMs - startMs);
		
		log.info("───────────────────────────────────────────────────────────");
		
		
		return obj;
		
	}
	
	// -------------------------------------------------------------------
	
	// 예외 발생 후 수행되는 코드
	@AfterThrowing(pointcut = "@annotation(org.springframework.transaction.annotation.Transactional)",
			       throwing = "ex")
	public void transactionRollback(JoinPoint jp, Throwable ex) {
		
		log.info("*** 트랜젝션이 롤백됨 {} *******", jp.getSignature().getName());
		
		log.error("[롤백 원인] : {}", ex.getMessage() );
	}
			
	
	//-----------------------------------------------
	
	/** 접속자 IP 얻어오는 메서드 (인터넷에 흔히 돌아다님. 보조메서드의 성격을 띄고있음 )
	 * @param request
	 * @return ip
	 */
	private String getRemoteAddr(HttpServletRequest request) {
		
		String ip = null;
		
		ip = request.getHeader("X-Forwarded-For");
		
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
		}
		
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-RealIP");
		}
		
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("REMOTE_ADDR");
		}
		
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	
}
