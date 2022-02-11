package com.vartool.web.module.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringApplicationContext implements ApplicationContextAware {
	private static ApplicationContext CONTEXT;

	private Logger logger = LoggerFactory.getLogger(SpringApplicationContext.class);

	public SpringApplicationContext() {
		this.logger.info("init SpringApplicationContext");
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		CONTEXT = context;
	}

	public ApplicationContext getApplicationContext() {
		return CONTEXT;
	}
}
