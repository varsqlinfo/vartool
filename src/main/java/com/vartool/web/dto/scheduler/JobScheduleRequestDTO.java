package com.vartool.web.dto.scheduler;
import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.vartool.web.model.entity.cmp.CmpEntity;
import com.vartool.web.model.entity.scheduler.JobScheduleEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class JobScheduleRequestDTO implements Serializable{

	private static final long serialVersionUID = 1L;

	private String jobUid;
	
	@NotEmpty
	@Size(max=200)
	private String jobName;
	
	@NotEmpty
	@Size(max=200)
	private String jobGroup;
	
	@NotEmpty
	private String cmpId; 

	@Size(max=250)
	private String jobDescription; 

	@Size(max=120)
	private String cronExpression;
	
	private String jobData;
	
	public JobScheduleEntity toEntity() {
		CmpEntity cmpEntity = new CmpEntity();
		cmpEntity.setCmpId(cmpId);
		
		return JobScheduleEntity.builder()
			.jobUid(jobUid)
			.jobName(jobName)
			.jobGroup(jobGroup)
			.cmpInfo(cmpEntity )
			.cronExpression(cronExpression)
			.jobDescription(jobDescription)
			.jobData(jobData)
			.build();
	}

}