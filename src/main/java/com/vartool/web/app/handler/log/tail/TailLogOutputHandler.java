package com.vartool.web.app.handler.log.tail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jknack.handlebars.internal.lang3.StringUtils;
import com.vartool.web.app.handler.log.LogCmpManager;
import com.vartool.web.app.websocket.service.WebSocketServiceImpl;
import com.vartool.web.constants.VartoolConstants;
import com.vartool.web.dto.response.CmpLogResponseDTO;
import com.vartool.web.dto.websocket.LogMessageDTO;
import com.vartool.web.module.VartoolUtils;

/**
 * tail output handler
* 
* @fileName	: TailLogOutputHandler.java
* @author	: ytkim
 */
public class TailLogOutputHandler implements Runnable {
	
	private static final Logger logger = LoggerFactory.getLogger("commandLog");

	private final WebSocketServiceImpl webSocketServiceImpl;
	private final long delayTime;
	private final String cmpId;
	
	private Tail tail;
	
	public TailLogOutputHandler(final WebSocketServiceImpl webSocketServiceImpl, CmpLogResponseDTO logDto, long delayTime) {
		this.webSocketServiceImpl = webSocketServiceImpl;
		this.delayTime = delayTime; 
		this.cmpId = logDto.getCmpId();
		
		String charset = logDto.getCharset();
		charset = StringUtils.isBlank(charset) ? VartoolConstants.CHAR_SET : charset;
		
		tail = new Tail(logDto.getLogPath(), 50000, new TailOutputStream(), charset);
		new Thread(tail).start();
		
		LogCmpManager.getInstance().setTailInfo(cmpId, tail);
	}

	/**
	 * {@inheritDoc}
	 */
	public void run() {
		
		while (true) {
			
			if(this.tail.isStop()) break; 
			
			String msg = this.tail.getOutputStream().getLog();
			if (!"".equals(msg)) {
				LogCmpManager.getInstance().addLogInfo(cmpId, msg);
				webSocketServiceImpl.sendMessage(LogMessageDTO.builder().log(msg).cmpId(this.cmpId).build(), VartoolUtils.getAppRecvId(this.cmpId));
			}
			
			try {
				Thread.sleep(delayTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage() ,e);
			}
		}
	}
}