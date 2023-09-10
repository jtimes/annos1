package com.postinforg.annoscoreapi.fw;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

//	@Value("${media.localpath}") private String localPath;
//	@Value("${media.urlpath}") private String urlPath;
	
	@Autowired
	CommonInterceptor commonInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(commonInterceptor)
//				.addPathPatterns("/**/api*")
				.addPathPatterns("/**/*")
				.excludePathPatterns("/resource/**")
				.excludePathPatterns("/login/**");

		// TODO Auto-generated method stub
//		WebMvcConfigurer.super.addInterceptors(registry);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
//		registry.addMapping("/**").allowedOrigins("http://localhost:8080", "http://localhost:8081");
		registry.addMapping("/**").allowedOrigins("*");
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// TODO Auto-generated method stub
//		WebMvcConfigurer.super.addResourceHandlers(registry);
//		registry.addResourceHandler(urlPath).addResourceLocations(localPath);
	}
}
