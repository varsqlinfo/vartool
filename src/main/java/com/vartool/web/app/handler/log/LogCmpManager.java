package com.vartool.web.app.handler.log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.jknack.handlebars.internal.antlr.misc.FlexibleHashMap.Entry;
import com.vartool.web.app.handler.CmpManager;
import com.vartool.web.app.handler.log.reader.LogReader;
import com.vartool.web.constants.AppCode.LOG_STATE;
import com.vartool.web.dto.response.CmpMonitorDTO;
import com.vartool.web.dto.vo.LogInfo;
import com.vartool.web.dto.websocket.LogMessageDTO;
import com.vartool.web.module.VartoolUtils;
import com.vartool.web.module.spring.SpringBeanFactory;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * log component manager
* 
* @fileName	: LogCmpManager.java
* @author	: ytkim
 */
public class LogCmpManager implements CmpManager {
	
	private final Map<String, TailLogStatus> TAIL_LOG_INFO= new ConcurrentHashMap<String, TailLogStatus>(); 
	
	private LogCmpManager() {}
	
	public static LogCmpManager getInstance() {
		return LogStatusInfoHolder.instance;
	}
	
	private static class LogStatusInfoHolder {
		private static final LogCmpManager instance = new LogCmpManager();
	}

	public boolean existsLog(String cmpId) {
		return TAIL_LOG_INFO.containsKey(cmpId);
	}
	
	public String getLogContent(String cmpId) {
		if(existsLog(cmpId)) {
			return TAIL_LOG_INFO.get(cmpId).getLogInfo().allLog();
		}
		return "";
	}
	
	public synchronized void createLogInfo(String cmpId, LogReader tail) {
		if(existsLog(cmpId)) {
			TAIL_LOG_INFO.get(cmpId).setStatus(true);
			
			if(tail != null) {
				if(TAIL_LOG_INFO.get(cmpId).getLogReader() != null) {
					TAIL_LOG_INFO.get(cmpId).getLogReader().stop();
					TAIL_LOG_INFO.get(cmpId).setLogReader(null);
				}
				
				TAIL_LOG_INFO.get(cmpId).setLogReader(tail);
			}
			return ; 
		}
		
		TAIL_LOG_INFO.put(cmpId, TailLogStatus.builder().logInfo(new LogInfo(1000)).logReader(tail).build());
	}
	
	public void addLogInfo(String cmpId, String logText) {
		if(existsLog(cmpId)) {
			TAIL_LOG_INFO.get(cmpId).getLogInfo().add(logText);
		}
	}
	
	public boolean isTailInfo(String cmpId) {
		return existsLog(cmpId) && TAIL_LOG_INFO.get(cmpId).isStatus();
	}
	
	public void removeLogInfo(String cmpId) {
		if(isTailInfo(cmpId)) {
			TailLogStatus info = TAIL_LOG_INFO.get(cmpId);
			info.setStatus(false);
			info.getLogReader().stop();
			info.setLogReader(null);
			info.getLogInfo().clear();
			
			TAIL_LOG_INFO.remove(cmpId);
		}
	}
	
	public synchronized void exception(String cmpId, String msg) {
		if(existsLog(cmpId)) {
			
			msg = msg+"\nlog restart\n";
			
			SpringBeanFactory.getWebSocketService().sendMessage(
				LogMessageDTO.builder().log(msg).cmpId(cmpId).build(), VartoolUtils.getAppRecvId(cmpId)
			);
			
			TailLogStatus info = TAIL_LOG_INFO.get(cmpId);
			info.setStatus(false);
			
			if(info.getLogReader() != null) {
				info.getLogReader().stop();
				info.setLogReader(null);
			}
			
			addLogInfo(cmpId, msg);
		}
	}
	
	public int getLogSize(String cmpId){
		if(existsLog(cmpId)) {
			return TAIL_LOG_INFO.get(cmpId).getLogInfo().getLogSize();
		}
		return 0;
	}
	
	public int getLogByte(String cmpId){
		if(existsLog(cmpId)) {
			return TAIL_LOG_INFO.get(cmpId).getLogInfo().getLogSize();
		}
		return 0;
	}
	
	public void clearLogInfo(String cmpId) {
		if(existsLog(cmpId)) {
			TAIL_LOG_INFO.get(cmpId).getLogInfo().clear();
		}
	}
	
	public void stopTail(String cmpId) {
		if(existsLog(cmpId)) {
			TAIL_LOG_INFO.get(cmpId).setStatus(false);
			if(TAIL_LOG_INFO.get(cmpId).getLogReader() != null) {
				TAIL_LOG_INFO.get(cmpId).getLogReader().stop();
				TAIL_LOG_INFO.get(cmpId).setLogReader(null);
			}
		}
	}
	
	public boolean isStartTail(String cmpId) {
		return existsLog(cmpId) && TAIL_LOG_INFO.get(cmpId).isStatus();
	}
	
	public boolean isStopTail(String cmpId) {
		if(existsLog(cmpId) && !TAIL_LOG_INFO.get(cmpId).isStatus()) {
			return true; 
		}
		
		return false;
	}

	public void setLogMonitorData(CmpMonitorDTO lmd) {
		String cmpId = lmd.getCmpId();
		
		if(existsLog(cmpId)) {
			TailLogStatus logInfo = TAIL_LOG_INFO.get(cmpId);
			
			lmd.setRunning(isStartTail(cmpId));
			lmd.setCurrentLogSize(logInfo.getLogInfo().getLogSize());
		}
		
	}

	public void setTailInfo(String cmpId, LogReader logReader) {
		if(existsLog(cmpId)) {
			TAIL_LOG_INFO.get(cmpId).setStatus(true);
			TAIL_LOG_INFO.get(cmpId).setLogReader(logReader);
			return ; 
		}
	}
	
	public void shutdown() {
		for(java.util.Map.Entry<String, TailLogStatus> item: TAIL_LOG_INFO.entrySet()) {
			if(item.getValue() != null  && item.getValue().getLogReader() != null) {
				item.getValue().getLogReader().stop();
			}
			
		}
	}
}

@Getter
@Setter
class TailLogStatus {
	private LogInfo logInfo;  
	private LogReader logReader;
	boolean status; 
	
	@Builder
	public TailLogStatus(LogInfo logInfo, LogReader logReader) {
		this.logInfo = logInfo;
		this.logReader = logReader; 
		this.status = true; 
	}
}

