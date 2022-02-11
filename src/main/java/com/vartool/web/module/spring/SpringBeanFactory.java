package com.vartool.web.module.spring;

import org.springframework.context.ApplicationContext;

import com.vartool.web.app.websocket.service.WebSocketServiceImpl;
import com.vartool.web.constants.ResourceConfigConstants;

public final class SpringBeanFactory {
	private SpringApplicationContext springApplicationContext;
	
	private SpringBeanFactory() {
		this.springApplicationContext = new SpringApplicationContext();
	}
	
	private static class Holder {
		private static final SpringBeanFactory instance = new SpringBeanFactory();
	}
	
	private static ApplicationContext getApplicationContext() {
		return Holder.instance.springApplicationContext.getApplicationContext();
	}

	public static Object getBean(String beanId) {
		return getApplicationContext().getBean(beanId);
	}

	public static <T> T getBean(String beanName, Class<T> requiredType) {
		return (T) getApplicationContext().getBean(beanName, requiredType);
	}
	
	public static WebSocketServiceImpl getWebSocketService() {
		return (WebSocketServiceImpl) getBean(ResourceConfigConstants.APP_WEB_SOCKET_SERVICE);
	}
	

}
