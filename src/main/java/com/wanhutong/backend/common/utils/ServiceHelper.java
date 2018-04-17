package com.wanhutong.backend.common.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * @author Administrator
 * 获取Spring容器中的service bean
 */
public final class ServiceHelper {

	public static final ApplicationContext WEB_APP_CONTEXT = new ClassPathXmlApplicationContext("spring-context.xml");


	@SuppressWarnings("unchecked")
	public static <T> T getService(String serviceName) {
		//WebApplicationContextUtils.
		return (T) WEB_APP_CONTEXT.getBean(serviceName);
	}

}
