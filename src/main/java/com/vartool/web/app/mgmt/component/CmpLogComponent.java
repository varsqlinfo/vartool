package com.vartool.web.app.mgmt.component;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vartool.web.app.handler.log.LogCmpManager;
import com.vartool.web.app.handler.log.tail.TailLogOutputHandler;
import com.vartool.web.app.websocket.service.WebSocketServiceImpl;
import com.vartool.web.dto.response.CmpLogResponseDTO;
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
	
	public Object startAppLog(String cmpId, CmpLogResponseDTO dto) {
	
		String logPath = dto.getLogPath(); 
		if(!StringUtils.isBlank(logPath) && (new File(logPath).exists() || LogFilenameUtils.isIncludePattern(logPath))) {
			LogCmpManager.getInstance().createLogInfo(cmpId, null);
			new Thread(new TailLogOutputHandler(webSocketServiceImpl, dto, 1000)).start();
		}else {
			return LogMessageDTO.builder().cmpId(cmpId).log("log path not found : " + logPath).build();
		}
		
		return null;
	}
}
