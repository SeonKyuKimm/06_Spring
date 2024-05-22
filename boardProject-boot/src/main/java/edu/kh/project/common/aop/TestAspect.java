package edu.kh.project.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component // bean으로 등록할거다 라는 뜻
@Aspect // AspectJ에서 제공해줌 TEST가 끝나고 나면 ASPECT 를 막아주어 AOP 로의 동작을 막아주기도 함
@Slf4j // 로그를 찍을 수 있는 객체(Logger)생성 코드를 추가해줄 수 있다 (lombok) 제공.
public class TestAspect {

	//괄호에는 PointCut을 작성해준다 . 이 어드바이스를 작성할 지점을 설정해주는것이라고요
	@Before( "execution(* edu.kh.project..*Controller*.*(..) )" ) 
	public void testAdvice(  ) {
		
		log.info(" --------- testAdvice() 수행됨 --------- ");
	}
	
	@After("execution(* edu.kh.project..*Controller*.*(..) )")
	public void controllerEnd(JoinPoint jp) {
		// JointPoint: AOP 기능이 적영된 대상
		
		// AOP가 적용된 클래스 이름 얻어오기
		String className = jp.getTarget().getClass().getSimpleName(); // ex) MainController
		
		// 실행된 컨트롤러 안에 있는 메서드의 이름을 얻어오기
		String methodName = jp.getSignature().getName(); // 메서드명이 나옴. mainPage 를 반환해줌
		
		log.info("-------------{}.{} 수행완료 --------------", className,methodName);
				
	}
	
	
	
}
