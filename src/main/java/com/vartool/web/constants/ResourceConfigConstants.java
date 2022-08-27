package com.vartool.web.constants;

/**
 * application resource constants
* 
* @fileName	: ResourceConfigConstants.java
* @author	: ytkim
 */
public interface ResourceConfigConstants {
	
	final String APP_DB_RESOURCE ="vartoolAppSqlSession";
	
	final String APP_DATASOURCE ="vartoolDataSource";
	
	final String APP_TRANSMANAGER ="transactionManager";
	
	final String APP_BATCH_TRANSMANAGER ="varBatchTransManager";
	
	//web socket task executor
	final String APP_WEB_SOCKET_TASK_EXECUTOR ="vartoolWebSocketTaskExecutor";
	
	final String APP_LOG_TASK_EXECUTOR ="vartoolLogTaskExecutor";
	
	final String APP_PASSWORD_ENCODER ="vartoolPasswordEncoder";
	
	final String APP_MODEL_MAPPER ="vartoolModelMapper";
	
	final String APP_WEB_SOCKET_SERVICE ="vartoolSocketService";
	
	final String MAIL_SERVICE = "mainService";
	
	final String USER_DETAIL_SERVICE = "vartoolUserService";
	
	
	final String APP_SCHEDULER = "vartoolScheduler";
}
