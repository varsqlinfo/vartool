package com.vartool.web.app.websocket.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.vartech.common.utils.VartechUtils;
import com.vartool.web.constants.ResourceConfigConstants;
import com.vartool.web.constants.WebSocketConstants;
import com.vartool.web.dto.websocket.LogMessageDTO;

/**
 * web socket service
* 
* @fileName	: WebSocketServiceImpl.java
* @author	: ytkim
 */
@Service(ResourceConfigConstants.APP_WEB_SOCKET_SERVICE)
public class WebSocketServiceImpl{
	private final Logger logger = LoggerFactory.getLogger(WebSocketServiceImpl.class);
	
	@Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
	
	final private static String USER_PREFIX = WebSocketConstants.Type.USER.getPrefix();
	
	@Async(ResourceConfigConstants.APP_WEB_SOCKET_TASK_EXECUTOR)
	public void sendMessage(LogMessageDTO msg, String... recvIds) {
		
		if(logger.isDebugEnabled()) {
			//logger.debug("websocket send message : {} , receivers : {}", msg, StringUtils.join(',',usrIds));
		}
		
		String jsonMsg = VartechUtils.objectToJsonString(msg); 
		
		for(String recvId : recvIds){
			this.simpMessagingTemplate.convertAndSend(USER_PREFIX+"/" +recvId, jsonMsg);
		}
	}
}