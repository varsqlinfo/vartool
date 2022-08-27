package com.vartool.web.app.scheduler.bean;

import org.quartz.JobExecutionContext;

import com.vartool.web.dto.JobResultVO;
import com.vartool.web.dto.scheduler.JobScheduleVO;

public interface JobService{
	public JobResultVO doExecute(JobExecutionContext context, JobScheduleVO jsv) throws Exception;
}
