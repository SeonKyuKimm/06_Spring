package edu.kh.project.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.MultipartConfigElement;

@Configuration // 프로젝트 전반적으로 설정파일임을 명시하는 어노테이션. 오타 하나라도 있음 서버안켜짐.
@PropertySource("classpath:/config.properties")
public class FileConfig implements WebMvcConfigurer {

	// WebMvcConfigurer : Spring MVC 프레임워크에서 제공하는 인터페이스 중 하나로,
	// 스프링 구성을 커스터마이징하고 확장하기 위한 메서드를 제공함
	// 주로 웹 어플리케이션의 설정을 조정하거나 추가하는데 사용함
	
	// 파일 업로드임계값
	@Value("${spring.servlet.multipart.file-size-threshold}")
	private long fileSizeThreshold; 
	
	// 요청당 파일 최대 크기
	@Value("${spring.servlet.multipart.max-request-size}")
	private long maxRequestSize;
	
	// 개별 파일당 최대 크기
	@Value("${spring.servlet.multipart.max-file-size}")
	private long maxFileSize;
	
	// 파일 최대 임계값이 초과되면 임시 저장할 폴더 경로
	@Value("${spring.servlet.multipart.location}")
	private String location;
	
	
	//--------------------------------------------------
	// 프로필 이미지에 관련된 ~
	@Value("${my.profile.resource-handler}")
	private String profileResourceHandler;
	
	@Value("${my.profile.resource-location}")
	private String profileResourceLocation;
	
	// --------------------------------------------------
	
	// 게시판 이미지 관련
	@Value("${my.board.resource-handler}") // config.prop에 설정된 Key 를 가져오면 Value 의 주소가 들어옴
	private String boardResourceHandler; // 요청주소 필드
	
	@Value("${my.board.resource-location}")
	private String boardResourceLocation; // 연결될 서버 폴더 경로 필드
	
	
	
	
	
	// 요청 주소에 따라
	// 서버 컴퓨터의 어떤 경로에 접근할지 설정
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		registry.addResourceHandler("/myPage/file/**") // 클라이언트의 요청 주소 패턴
		.addResourceLocations("file:///C:/uploadFiles/test/");
		// Client가 /myPage/file/**패턴으로 이미지를 요청할 때,
		// 요청을 연결해서 처리해 줄 서버 폴더 경로로 연결
		
		// 프로필 이미지 요청 - 서버 폴더 연결 추가 
		registry.
		 addResourceHandler(profileResourceHandler) //  /myPage/profile/**
		.addResourceLocations(profileResourceLocation); //  file:///C:/uploadFiles/profile/
		
		// file:///C: 는 파일 시스템의 루트 디렉토리
		
		// file://  은 URL 스킴(scheme), 파일 시스템의 Resource(자원)를 말한다.
		// /C: 는 Windows 시스템에서 C드라이브를 가리킨다.
		// file:///C: "C드라이브의 루트 디렉토리"를 의미한다.
		
		//-----------------------------------------------------
		// 게시글 이미지 요청 - 서버 폴더 연결 추가
		registry
		.addResourceHandler(boardResourceHandler)
		.addResourceLocations(boardResourceLocation);
		
	}
	
	
	/* MultiPartResolver 에 대한 설정*/
	@Bean
	public MultipartConfigElement configElement() {
		// MultipartConfigElement :
		// 파일 업로드를 처리하는데 사용되는 MultipartConfigElement 를 구성하고 반환해준다
		// 파일 업로드를 위한 구성 옵션을 설정하는데 사용
		// 업로드 파일의 최대 크기, 메모리에서의 임시 저장 경로 등을 설정할 수 있습니다
		// -> 보통은 서버 경로를 작성함 (ㄹㅇ 서버 컴퓨터의 경로 작성) => 보안상 문제가 있을 수 있기때문에
		// config.prop에 작성한 내용을 가져와서 쓸꺼다.
		
		MultipartConfigFactory factory = new MultipartConfigFactory(); // Config를 찍어낼 공장
		
		// MultipartConfigFactory가 공장이라면 밑에 애들은 그 공장에 대한 설비
		factory.setFileSizeThreshold(DataSize.ofBytes(fileSizeThreshold));// long 타입은 못받아줌
		
		factory.setMaxFileSize(DataSize.ofBytes(maxFileSize));			  // 그래서(DataSize.ofBytes
		
		factory.setMaxFileSize(DataSize.ofBytes(maxRequestSize));		  // 안에 넣어준다
		
		factory.setLocation(location); // 얘는 경로라 걍 써도 됨
		
		return factory.createMultipartConfig();
	}
	
	
	// multipartResolver를 반환하는 메소드. multipartResolver 객체를 Bean으로 추가함으로써
	// 									-> Spring 이 추가 후 , 위에서 만든 MultipartConfig 자동으로 이용함
	@Bean
	public MultipartResolver multipartResolver() {
		//MultipartResolver : MultipartFile을 처리해주는 해결사..
		//MultipartResolver는 클라이언트로부터 받은 멀티파트 요청을 처리하고,
		//이중에서 업로도듼 파일을 추출하여 MultipartFile 객체로 제공하는 역할을 함
		
		StandardServletMultipartResolver multipartResolver
			= new StandardServletMultipartResolver();
		
		return multipartResolver;
	}
	
}
