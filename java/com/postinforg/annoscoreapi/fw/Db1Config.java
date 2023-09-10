package com.postinforg.annoscoreapi.fw;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@MapperScan(basePackages="com.postinforg.annoscoreapi.mapper", sqlSessionFactoryRef="sqlSessionFactory")
public class Db1Config {
	
	@Value("${spring.datasource.url}") private String url;
	@Value("${spring.datasource.username}") private String username;
	@Value("${spring.datasource.password}") private String password;
	@Value("${spring.datasource.driverClassName}") private String driver;
	
	@Autowired
	private ApplicationContext context;

	@Bean
	@Primary
	public DataSource dataSource() throws Exception {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(driver);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		
		dataSource.setValidationQuery("select 1");
		dataSource.setTestOnBorrow(true);
		dataSource.setTestWhileIdle(true);
		dataSource.setTimeBetweenEvictionRunsMillis(1000 * 120);
		dataSource.setMinEvictableIdleTimeMillis(1000*60);
		return dataSource;
	}
	
	@Bean
	@Primary
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
		SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
		factory.setDataSource(dataSource);
//		factory.setDataSource(new Log4jdbcProxyDataSource(dataSource));
		factory.setConfigLocation(context.getResource("classpath:mybatis-config.xml"));
		factory.setMapperLocations(context.getResources("classpath:mapper/*.xml"));
		
		return factory.getObject();
	}

	@Bean
	@Primary
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory factory) throws Exception {
		return new SqlSessionTemplate(factory);
	}
	
	
}
