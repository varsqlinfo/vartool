package com.vartool.web.app.handler.deploy;

import java.util.Deque;

import com.vartool.web.app.websocket.service.WebSocketServiceImpl;
import com.vartool.web.dto.websocket.LogMessageDTO;
import com.vartool.web.module.Utils;

/**
 * Abstract Deploy
* 
* @fileName	: AbstractDeploy.java
* @author	: ytkim
 */
public abstract class AbstractDeploy implements DeployInterface{
	
	protected WebSocketServiceImpl webSocketServiceImpl;
	
	public AbstractDeploy(WebSocketServiceImpl webSocketServiceImpl) {
		this.webSocketServiceImpl = webSocketServiceImpl; 
	}
	
	public void sendLogMessage(LogMessageDTO msgData, String recvId) {
		Deque<String> msg = Utils.getNewlineSplitToDeque(msgData.getLog());
		msgData.setLog("");
		
		DeployCmpManager.getInstance().addLogInfo(msgData.getCmpId(), msg);
		msgData.setLogList(msg);
		webSocketServiceImpl.sendMessage(msgData, recvId);
	}
}
