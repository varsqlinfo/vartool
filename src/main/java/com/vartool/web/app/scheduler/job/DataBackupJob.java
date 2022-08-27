package com.vartool.web.app.scheduler.job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.vartool.web.app.scheduler.bean.JobBean;
import com.vartool.web.dto.JobResultVO;
import com.vartool.web.dto.scheduler.JobScheduleVO;

@Component
public class DataBackupJob extends JobBean {
	private final Logger logger = LoggerFactory.getLogger(DataBackupJob.class);

	@Override
	public JobResultVO doExecute(JobExecutionContext context, JobScheduleVO jsv) throws Exception {
		logger.debug("## data backup start : {}", jsv);
		
		return JobResultVO.builder().build(); 
	}
}