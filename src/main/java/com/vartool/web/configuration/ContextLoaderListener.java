package com.vartool.web.configuration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.vartool.web.app.config.VartoolConfiguration;

/**
 * -----------------------------------------------------------------------------
* @fileName		: ContextLoaderListener.java
* @desc		: servlet listener
* @author	: ytkim
*-----------------------------------------------------------------------------
  DATE			AUTHOR			DESCRIPTION
*-----------------------------------------------------------------------------
*2020. 4. 21. 			ytkim			최초작성

*-----------------------------------------------------------------------------
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