package com.postinforg.annoscoreapi.fw;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class CommonInterceptor implements HandlerInterceptor {

	static final Logger logger = LoggerFactory.getLogger(CommonInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		String referer = request.getHeader("referer");
		String targetUrl = request.getRequestURI();
		String contextPath = request.getContextPath();	// /api
		
		logger.debug(">>> referer : " + referer + ", target : " + targetUrl);
		
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}
	
}
