package com.vartool.web.configuration;

import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.LoggerFactory;

import com.vartool.web.app.handler.log.LogCmpManager;

import ch.qos.logback.classic.LoggerContext;

public class ShutdownHookConfiguration {

    public void destroy() {
    	try {
    		LogCmpManager.getInstance().shutdown();
    	}catch(Exception e) {
    		System.out.println("LogCmpManager shutdown error : "+ e.getMessage());
    	}
    	
    	
    	// quartz shutdown
		try {
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.shutdown(true);
			Thread.sleep(1000);
		}catch(Exception e) {
			System.out.println("quartz shutdown error : "+ e.getMessage());
		}
		
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.stop();
        
        
        

    }
}