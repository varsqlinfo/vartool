package com.vartool.web.app.handler.command;

import org.apache.commons.exec.ExecuteWatchdog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vartool.web.app.websocket.service.WebSocketServiceImpl;
import com.vartool.web.dto.response.CmpCommandResponseDTO;
import com.vartool.web.dto.websocket.LogMessageDTO;
import com.vartool.web.module.VartoolUtils;

/**
 * output handler
* 
* @fileName	: CommandLogOutputHandler.java
* @author	: ytkim
 */
public class CommandLogOutputHandler implements Runnable {
	
	private static final Logger logger = LoggerFactory.getLogger("commandLog");

	private final WebSocketServiceImpl webSocketServiceImpl;
	private final ExecuteWatchdog watchdog;
	private final CommandByteOutputStream output;
	private final long delayTime;
	private final String cmpId;
	
	private String recvId; 
	
	private boolean stop = false; 
	
	public CommandLogOutputHandler(final WebSocketServiceImpl webSocketServiceImpl, CmpCommandResponseDTO dto, String cmdMode, ExecuteWatchdog watchdog, CommandByteOutputStream output, long delayTime) {
		this.webSocketServiceImpl = webSocketServiceImpl;
		this.watchdog = watchdog;
		this.output = output; 
		this.delayTime = delayTime; 
		this.cmpId = dto.getCmpId();
		this.recvId = VartoolUtils.getCommandRecvId(this.cmpId);
		
		CommandCmpManager.getInstance().createLogInfo(this.cmpId);
		CommandCmpManager.getInstance().enableCommand(this.cmpId, cmdMode, this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		
		while (true) {
			
			String msg = output.getLog();
			if (!"".equals(msg)) {
				CommandCmpManager.getInstance().addLogInfo(this.cmpId, msg);
				webSocketServiceImpl.sendMessage(LogMessageDTO.builder().log(msg).cmpId(this.cmpId).build(), this.recvId);
			}
			
			if(isStop()) {
				CommandCmpManager.getInstance().disableCommand(cmpId);
				break;
			}
			
			try {
				if(!watchdog.isWatching()) {
					CommandCmpManager.getInstance().disableCommand(cmpId);
					break; 
				}
			}catch(Exception e) {
				CommandCmpManager.getInstance().disableCommand(cmpId);
				logger.error(e.getMessage() ,e);
				break;
			}
			
			try {
				Thread.sleep(delayTime);
			} catch (InterruptedException e) {
				logger.error(e.getMessage() ,e);
			}
		}
	}

	public boolean isStop() {
		return stop;
	}

	public void stop() {
		this.stop = true;
		watchdog.destroyProcess();
		watchdog.stop();
	}
}