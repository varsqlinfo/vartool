package com.vartool.web.app.mgmt.component;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vartool.web.app.handler.log.LogCmpManager;
import com.vartool.web.app.handler.log.reader.FileReader;
import com.vartool.web.app.handler.log.reader.SshReader;
import com.vartool.web.app.handler.log.stream.LogOutputHandler;
import com.vartool.web.app.websocket.service.WebSocketServiceImpl;
import com.vartool.web.constants.LogType;
import com.vartool.web.dto.ReadLogInfo;
import com.vartool.web.dto.websocket.LogMessageDTO;
import com.vartool.web.module.LogFilenameUtils;

/**
 * 로그 컴포넌트  
* 
* @fileName	: CmpLogComponent.java
* @author	: ytkim
 */
@Component
public class CmpLogComponent {
	private final static Logger logger = LoggerFactory.getLogger(CmpLogComponent.class);
	
	@Autowired
	private WebSocketServiceImpl webSocketServiceImpl;
	
	public LogMessageDTO startLog(String cmpId, ReadLogInfo readLogInfo) {
		
		logger.debug("startLog cmpId: {}, logName :{}", cmpId , readLogInfo.getName());
		
		if(LogType.SSH.name().equals(readLogInfo.getLogType())) {
			return startSshLog(cmpId, readLogInfo);
		}else {
			return startFileLog(cmpId, readLogInfo);
		}
	}
	
	private LogMessageDTO startFileLog(String cmpId, ReadLogInfo readLogInfo) {
		String logPath = readLogInfo.getLogPath(); 
		if( !(!StringUtils.isBlank(logPath) 
			&& (new File(logPath).exists() || LogFilenameUtils.isIncludePattern(logPath))) ) {
			return LogMessageDTO.builder().cmpId(cmpId).log("log path not found : " + logPath).build();
		}
		
		LogCmpManager.getInstance().createLogInfo(cmpId, null);
		new Thread(new LogOutputHandler(webSocketServiceImpl, readLogInfo, 1000, new FileReader(readLogInfo))).start();
		
		return LogMessageDTO.builder().cmpId(cmpId).state(999).build();
	}
	
	private LogMessageDTO startSshLog(String cmpId, ReadLogInfo readLogInfo) {
		
		LogCmpManager.getInstance().createLogInfo(cmpId, null);
		new Thread(new LogOutputHandler(webSocketServiceImpl, readLogInfo, 1000, new SshReader(readLogInfo) )).start();
		
		return LogMessageDTO.builder().cmpId(cmpId).state(999).build();
	}

	
}
