package com.vartool.web.dto.scheduler;
import java.io.Serializable;

import com.vartool.web.model.entity.scheduler.JobScheduleEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class JobScheduleVO implements Serializable{

	private static final long serialVersionUID = 1L;

	private String jobUid;
	
	private String jobName;
	
	private String jobGroup;
	
	private String cmpId; 

	private String jobDescription; 

	private String cronExpression;
	
	private String jobData;
	
	@Builder
	public JobScheduleVO(String jobUid, String jobName, String jobGroup, String cmpId, String jobDescription, 
			String cronExpression, String jobData){
		this.jobUid = jobUid;
		this.jobName = jobName;
		this.jobGroup = jobGroup;
		this.cmpId = cmpId;
		this.jobDescription = jobDescription;
		this.cronExpression = cronExpression;
		this.jobData = jobData;
	}
	
	public static JobScheduleVO toVo(JobScheduleRequestDTO dto) {
		return JobScheduleVO.builder()
			.jobUid(dto.getJobUid())
			.jobName(dto.getJobName())
			.jobGroup(dto.getJobGroup())
			.cmpId(dto.getCmpId())
			.cronExpression(dto.getCronExpression())
			.jobDescription(dto.getJobDescription())
			.jobData(dto.getJobData())
			.build();
	}
	
	public static JobScheduleVO toVo(JobScheduleEntity entity) {
		return JobScheduleVO.builder()
				.jobUid(entity.getJobUid())
				.jobName(entity.getJobName())
				.jobGroup(entity.getJobGroup())
				.cmpId(entity.getCmpInfo().getCmpId())
				.cronExpression(entity.getCronExpression())
				.jobDescription(entity.getJobDescription())
				.jobData(entity.getJobData())
				.build();
	}

}