package com.vartool.web.configuration.websocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import com.vartech.common.utils.VartechUtils;
import com.vartool.web.app.handler.log.LogCmpManager;
import com.vartool.web.app.user.service.CmpLogService;
import com.vartool.web.constants.WebSocketConstants;
import com.vartool.web.dto.websocket.LogMessageDTO;

/**
 * web event
* 
* @fileName	: WebSocketEventListener.java
* @author	: ytkim
 */
@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
    
    final private static String USER_QUEUE_PREFIX = WebSocketConstants.USER_DESTINATION_PREFIX + WebSocketConstants.Type.USER_QUEUE_LOG.getClientDestination();
    
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @Autowired
	private CmpLogService cmpLogService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
    	StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
		logger.info("[Connected] : {}", sha.getUser().getName());
        logger.info("Received a new web socket connection");  
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    	StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
		logger.info("[Disonnected] {} " , sha.getUser().getName());
    }
    
	@EventListener
	public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
		
		Message<byte[]> message = event.getMessage();
		
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String destination = accessor.getDestination();
		
		String logCmpId = String.valueOf(accessor.getHeader(SimpMessageHeaderAccessor.SUBSCRIPTION_ID_HEADER));
		
		if(destination.startsWith(USER_QUEUE_PREFIX)) {
			
			String sessionId = accessor.getSessionId();
			String jsonMsg;
			if (LogCmpManager.getInstance().existsLog(logCmpId)) { // log 실행중일때.
				
				jsonMsg = VartechUtils.objectToJsonString(LogMessageDTO.builder().cmpId(logCmpId).log(LogCmpManager.getInstance().getLogContent(logCmpId)).build());
				//logger.debug("sessionId :{}, destination:{}",sessionId,  destination);
			}else {
				jsonMsg = VartechUtils.objectToJsonString(LogMessageDTO.builder().cmpId(logCmpId).log("log loading...").build());
				cmpLogService.loadLog(logCmpId, false);
			}
						
			simpMessagingTemplate.convertAndSendToUser(sessionId, WebSocketConstants.Type.USER_QUEUE_LOG.getClientDestination()+ "/" + logCmpId, jsonMsg, createHeaders(sessionId));
		}
	}
	
	
	private MessageHeaders createHeaders(String sessionId) {
		SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor
				.create(SimpMessageType.MESSAGE);
		headerAccessor.setSessionId(sessionId);
		headerAccessor.setLeaveMutable(true);
		return headerAccessor.getMessageHeaders();

	}
}