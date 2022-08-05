package com.vartool.web.configuration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.vartool.core.config.VartoolConfiguration;

/**
 * web 설정.
* 
* @fileName	: ContextLoaderListener.java
* @author	: ytkim
 */
public class ContextLoaderListener  implements ServletContextListener{
	public void contextInitialized(ServletContextEvent event)
    {
		//ServletContext sc = event.getServletContext();
		VartoolConfiguration.getInstance();
	}

	public void contextDestroyed(ServletContextEvent event){
		System.out.println("contextDestroyed-------------------------");
	}
}