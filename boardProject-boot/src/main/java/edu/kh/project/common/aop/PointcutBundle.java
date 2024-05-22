package edu.kh.project.common.aop;

import org.aspectj.lang.annotation.Pointcut;

// Pointcut을 모아두는 클래스 용도로 만듬
// Pointcut : 실제 advice가 적용될 지점이다
// 패턴을 작성하는것이 좀 번거로와서 이름만 불러다 쓸 예정.
public class PointcutBundle {

	// 작성하기 어려운 포인트컷을 미리 작성해두고 필요한곳에서 클래스명. 메서드명() 으로 호출해서 사용 가능
	
	// ex ) @After("execution(* edu.kh.project..*Controller*.*(..) )")
	    //= @Before("PointcutBundle.controllerPointCut() ")

	@Pointcut("execution(* edu.kh.project..*Controller*.*(..) )")
	public void controllerPointCut() {}
	
	@Pointcut("execution(* edu.kh.project..*ServiceImpl*.*(..) )")
	public void serviceImplPointCut() {}
	
	
}
