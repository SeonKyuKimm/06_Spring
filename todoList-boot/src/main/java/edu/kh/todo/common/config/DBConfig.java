package edu.kh.todo.common.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/* @Configuration
  
	설정용 클래스임을 명시하는 어노테이션
	+ 객체로 생성해서 내부 코드를 서버 실행시 모두 수행
	
   @PropertySource
   - 지정된 경로의 properties 파일 내용을 읽어와 사용
   - 사용할 properties 파이리 다수일 경우 해당 어노테이션 (@PropertySource)
   	 연속해서 작성하면 된다
   
	@ConfigurationProperties(prefix="spring.datasource.hikari")
	-@PropertySource 로 읽어온 properties 파일의 내용 중
	 접두사 (앞부분, prefix)가 일치하는 값만 읽어옴
	 
	@Bean
	- 개발자가 수동으로 생성한 객체의 관리를
	  스프링에게 넘기는 어노테이션
	  (Bean으로 등록해달라~) 

	DataSource : Connection 생성 + Connection Pool 지원하는 객체를 참조하기 위한
				 Java 인터페이스
				 (DriverManager 대안 , Java JNDI 기술이 적용된 객체)
				 
	@AutoWired : 등록된 Bean 중에서 타입이 일치하거나, 상속관계에 있는 Bean을
				 지정된 필드에 주입하는것 == 의존성 주입(DI, Dependency Injection, IOC와 밀접한 관련 기술)
*/

@Configuration 
@PropertySource("classpath:/config.properties")
public class DBConfig {

	// 필드
	//import org.springframework.context.ApplicationContext;
	@Autowired // ( DI, 의존성 주입 )
	private ApplicationContext applicationContext; // application Scpoe 객체 : 즉, 현재 프로젝트를 나타내는것.
	
	///////////////// HikariCP 설정 ///////////////////
	
  //@Bean - 객체를 Bean 에게 넘겨 Spring이 관리하게 만드는 어노테이션
	@Bean				   //prefix = 접두사
	@ConfigurationProperties(prefix="spring.datasource.hikari")
	public HikariConfig hikariconfig() {
				
		return new HikariConfig();
	}
	
	@Bean
	public DataSource dataSource(HikariConfig config) {
		// 매개변수 hikariConfig config 라는건
		// 등록된 Bean중에 HikariConfig 타입의 Bean 자동으로 주입되고 있다.
		
		DataSource dataSource = new HikariDataSource(config);
		return dataSource;
		
	}
	
	///////////////// MyBatis 설정 ///////////////////
	
	public SqlSessionFactory sessionFactory(DataSource dataSource) throws Exception {
		
		SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
		
		sessionFactoryBean.setDataSource(dataSource);
		
		// mapper.xml(SQL문) 파일이 모이는 경로 지정
		// -> Mybatis 코드 수행시 mapper.xml 을 읽을 수 있다.
		// sessionFactoryBean.setMapperLocations("현재프로젝트.자원.어떤파일");
		
		sessionFactoryBean.setMapperLocations(
					applicationContext.getResources("classpath:/mappers/**.xml") );
		
		// 해당 패키지 내 모든 클래스의 별칭을 등록하는것
		// - Mybatis는 특정 클래스 지정 시 패키지명.클래스명을 모두 작성해야함
		// -> 그러기엔 너무 길다... 그래서 긴 이름을 짧게 부를 별칭을 설정할 수 있게끔 해둠
		
		// setTypeAliasesPackage("패키지") 이용 시
		// 클래스 파일명이 별칭으로 등록된다 (자동으로)
		
		// ex) edu.kh.todo.model.dto.Todo -- > Todo(별칭등록해서) 라고만 부를 수 있다		
		sessionFactoryBean.setTypeAliasesPackage("edu.kh.todo");
		
		// 마이바티스 설정 파일 경로 지정
		sessionFactoryBean.setConfigLocation(
					applicationContext.getResource("classpath:/mybatis-config.xml") );
		
		
		// 설정 내용이 모두 적용된 객체 반환
		return sessionFactoryBean.getObject();
		
		
	}
	
		// SqlSessionTemplate : Connection + DBCP + Mybatis지원객체 + 트랜젝션 제어 처리 
	@Bean
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sessionFactory) {
		
		return new SqlSessionTemplate(sessionFactory);
	}
	
	// DataSourceTransactionManager : 트랜잭션 매니저 ( 제어처리 / commit, rollback ? ) 
	@Bean
	public DataSourceTransactionManager dataSourceTransactionManager(DataSource dataSource) {
	
		return new DataSourceTransactionManager(dataSource);
	}
	
	
}

















