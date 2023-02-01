package com.vartool.web.model.entity.scheduler;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vartool.web.model.entity.base.AabstractAuditorModel;
import com.vartool.web.model.entity.cmp.CmpEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@DynamicUpdate
@NoArgsConstructor
@Entity
@Table(name = JobScheduleEntity._TB_NAME)
@ToString(exclude = {})
public class JobScheduleEntity extends AabstractAuditorModel{

	private static final long serialVersionUID = 1L;

	public final static String _TB_NAME="VTQTZ_JOB_SCHEDULE";
	
	@Id
	@Column(name ="JOB_UID")
	private String jobUid;
	
	@Column(name ="JOB_NAME")
	private String jobName;
	
	@Column(name ="JOB_GROUP")
	private String jobGroup; 

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CMP_ID", nullable = false)
	private CmpEntity cmpInfo ;

	@Column(name ="JOB_DATA")
	private String jobData; 

	@Column(name ="CRON_EXPRESSION")
	private String cronExpression;
	
	@Column(name ="JOB_DESCRIPTION")
	private String jobDescription; 
	
	@Builder
	public JobScheduleEntity (String jobUid, String jobName, String jobGroup, CmpEntity cmpInfo, String jobData, String cronExpression, String jobDescription) {
		this.jobUid = jobUid;
		this.jobName = jobName;
		this.jobGroup = jobGroup;
		this.cmpInfo = cmpInfo;
		this.jobData = jobData;
		this.cronExpression = cronExpression;
		this.jobDescription = jobDescription;
	}

	public final static String JOB_UID="jobUid";
	public final static String JOB_NAME="jobName";
	public final static String JOB_GROUP="jobGroup";
	public final static String CMP_INFO="cmpInfo";
	public final static String JOB_DATA="jobData";
	public final static String CRON_EXPRESSION="cronExpression";
	public final static String JOB_DESCRIPTION="jobDescription";
	
}