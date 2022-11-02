package com.vartool.web.app.handler.log.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jknack.handlebars.internal.lang3.StringUtils;
import com.vartool.web.app.handler.log.LogCmpManager;
import com.vartool.web.app.handler.log.reader.FileReader;
import com.vartool.web.app.handler.log.reader.LogReader;
import com.vartool.web.app.handler.log.reader.SshReader;
import com.vartool.web.app.websocket.service.WebSocketServiceImpl;
import com.vartool.web.constants.LogType;
import com.vartool.web.constants.VartoolConstants;
import com.vartool.web.dto.ReadLogInfo;
import com.vartool.web.dto.websocket.LogMessageDTO;
import com.vartool.web.module.VartoolUtils;

/**
 * tail output handler
* 
* @fileName	: TailLogOutputHandler.java
* @author	: ytkim
 */
public class LogOutputHandler implements Runnable {
	
	private static final Logger logger = LoggerFactory.getLogger(LogOutputHandler.class);

	private final WebSocketServiceImpl webSocketServiceImpl;
	private final long delayTime;
	private final String cmpId;
	
	private LogReader logReader;
	
	public LogOutputHandler(final WebSocketServiceImpl webSocketServiceImpl, ReadLogInfo readLogInfo, long delayTime, LogReader logReader) {
		this.webSocketServiceImpl = webSocketServiceImpl;
		this.delayTime = delayTime; 
		this.cmpId = readLogInfo.getCmpId();
		this.logReader = logReader; 
		
		new Thread(this.logReader).start();
		
		LogCmpManager.getInstance().setTailInfo(cmpId, logReader);
	}

	/**
	 * {@inheritDoc}
	 */
	public void run() {
		
		while (true) {
			
			String msg = this.logReader.getOutputStream().getLog();
			if (!"".equals(msg)) {
				LogCmpManager.getInstance().addLogInfo(cmpId, msg);
				webSocketServiceImpl.sendMessage(LogMessageDTO.builder().log(msg).cmpId(this.cmpId).build(), VartoolUtils.getAppRecvId(this.cmpId));
			}
			
			if(this.logReader.isStop()) break; 
			
			try {
				Thread.sleep(delayTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage() ,e);
			}
		}
	}
}