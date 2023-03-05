package com.vartool.web.module;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import com.vartool.web.app.websocket.service.WebSocketServiceImpl;
import com.vartool.web.configuration.websocket.WebSocketBrokerConfig;
import com.vartool.web.dto.websocket.LogMessageDTO;

/**
 * log file name util
* 
* @fileName	: LogUtils.java
* @author	: ytkim
 */
public final class LogUtils {
	
	private static final int LOG_LIMIT_SIZE = WebSocketBrokerConfig.MESSAGE_SIZE_LIMIT - 5000;

	private LogUtils() {
	}


	public static void sendCollectionMsg(String cmpId, String recvId, Deque<String> msg, WebSocketServiceImpl webSocketServiceImpl) {
		int totalLogByte = 0; 
		List<String> sendLogList = new ArrayList<String>();
		for (Iterator<String> iterator = msg.iterator(); iterator.hasNext();) {
			String log = iterator.next();
			int byteLen =log.getBytes().length; 
			
			totalLogByte += byteLen;
			if(totalLogByte > LOG_LIMIT_SIZE) {
				totalLogByte = 0; 
				webSocketServiceImpl.sendMessage(LogMessageDTO.builder().logList(sendLogList).cmpId(cmpId).build(), recvId);
				sendLogList = new ArrayList<String>();
			}
			sendLogList.add(log);
		}
		
		if(sendLogList.size() > 0) {
			webSocketServiceImpl.sendMessage(LogMessageDTO.builder().logList(sendLogList).cmpId(cmpId).build(), recvId);
		}
	}
}
