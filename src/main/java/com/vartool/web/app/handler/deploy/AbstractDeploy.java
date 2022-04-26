package com.vartool.web.app.handler.deploy;

import com.vartool.web.app.websocket.service.WebSocketServiceImpl;
import com.vartool.web.constants.BlankConstants;
import com.vartool.web.dto.websocket.LogMessageDTO;

public abstract class AbstractDeploy implements DeployInterface{
	
	protected WebSocketServiceImpl webSocketServiceImpl;
	
	public AbstractDeploy(WebSocketServiceImpl webSocketServiceImpl) {
		this.webSocketServiceImpl = webSocketServiceImpl; 
	}
	
	public void sendLogMessage(LogMessageDTO msgData, String recvId) {
		DeployCmpManager.getInstance().addLogInfo(msgData.getCmpId(), msgData.getLog().endsWith(BlankConstants.NEW_LINE_CHAR) ? msgData.getLog() : msgData.getLog()+BlankConstants.NEW_LINE_CHAR);
		webSocketServiceImpl.sendMessage(msgData, recvId);
	}
}
