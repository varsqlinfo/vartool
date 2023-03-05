package com.vartool.web.app.handler.log.stream;

import java.util.Deque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vartool.web.app.handler.log.LogCmpManager;
import com.vartool.web.app.handler.log.reader.LogReader;
import com.vartool.web.app.websocket.service.WebSocketServiceImpl;
import com.vartool.web.dto.ReadLogInfo;
import com.vartool.web.module.LogUtils;
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
			
			Deque<String> msg = this.logReader.getOutputStream().getLog();
			if (msg.size() > 0) {
				LogCmpManager.getInstance().addLogInfo(cmpId, msg);
				LogUtils.sendCollectionMsg(this.cmpId, VartoolUtils.getAppRecvId(this.cmpId), msg, webSocketServiceImpl);
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