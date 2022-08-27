package com.vartool.web.app.scheduler.listener;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 
* 
* @fileName	: JobsListener.java
* @author	: ytkim
 */
@Component
public class JobsListenerImpl implements JobListener {
	private final Logger logger = LoggerFactory.getLogger(JobsListenerImpl.class);
	
	@Override
	public String getName() {
		return "vatoolJobListener";
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		//JobDetail jobDetail = context.getJobDetail(); 
		//JobKey jobKey = context.getJobDetail().getKey();
		
		//logger.debug("jobWasExecuted at jobKey: {} ,jobName: {} ,FireTime: {} ,JobRunTime: {} ,instanceid: {}", jobKey, jobKey.getName(), context.getFireTime(), context.getJobRunTime(), context.getFireInstanceId());
		
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		//JobKey jobKey = context.getJobDetail().getKey();
		//logger.debug("jobWasExecuted at jobKey: {} ,jobName: {} ,FireTime: {} ,JobRunTime: {}", jobKey, jobKey.getName(), context.getFireTime(), context.getJobRunTime());
		
	}
}
