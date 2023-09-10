package com.postinforg.annoscoreapi.fw;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@Aspect
public class FwAspect {
	
	private static final Logger logger = LoggerFactory.getLogger(FwAspect.class);
	

	@Pointcut("execution(* com.postinforg.annoscoreapi..*Controller.*(..))")
//	@Pointcut("execution(* com.postinforg.annoscoreapi..*Service.*(..))")
	public void controllerLogging() {
	}
	
	@Around("controllerLogging()")
	public Object controllerLoggingMethod(ProceedingJoinPoint pjp) throws Throwable {
		try {
			Object result = pjp.proceed();
			
			HttpServletRequest request = 
					((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
			
			String uri = request.getRequestURI();
			logger.debug(">>> uri : " + uri + " -----------------------------------");
			String className = pjp.getSignature().getDeclaringType().getSimpleName();
			logger.debug(">>> " + className + " : " + parameterMaps(request));
			
			return result;
		}
		catch(Throwable t) {
			throw t;
		}
	}
	
	private String parameterMaps(HttpServletRequest request) {
		String result = " : ";
		Map<String, String[]> r = request.getParameterMap();
		
		Iterator it = r.keySet().iterator();
		while(it.hasNext()) {
			result += "[";
			String key = (String) it.next();
			result += key + ": ";
			String[] v = r.get(key);
			for(int i=0; i<v.length; i++) {
				result += v[i] + ",";
			}
			
			result += "] ";
		}
		return result;
	}
	
}
