package com.vartool.web.configuration;

import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;

public class ShutdownHookConfiguration {

    public void destroy() {
    	
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