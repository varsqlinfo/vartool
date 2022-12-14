package com.vartool.web.configuration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.vartool.core.config.VartoolConfiguration;

/**
 * web 설정.
* 
* @fileName	: ContextLoaderListener.java
* @author	: ytkim
 */
public class ContextLoaderListener  implements ServletContextListener{
	
	
	private ConfigurableApplicationContext webApplicationContext;
	
	public void contextInitialized(ServletContextEvent event)
    {
		ServletContext sc = event.getServletContext();
		VartoolConfiguration.getInstance();
		
		webApplicationContext = (ConfigurableApplicationContext) WebApplicationContextUtils.getWebApplicationContext(sc);
	}

	public void contextDestroyed(ServletContextEvent event){
		System.out.println("contextDestroyed-------------------------");
		
		webApplicationContext.close();
		
		new ShutdownHookConfiguration().destroy();
		
	}
}