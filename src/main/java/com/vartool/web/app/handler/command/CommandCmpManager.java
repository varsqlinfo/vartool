package com.vartool.web.app.handler.command;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.vartool.web.app.handler.CmpManager;
import com.vartool.web.constants.CommandType;
import com.vartool.web.constants.AppCode.LOG_STATE;
import com.vartool.web.dto.response.CmpMonitorDTO;
import com.vartool.web.dto.vo.LogInfo;
import com.vartool.web.dto.websocket.LogMessageDTO;
import com.vartool.web.module.SecurityUtils;
import com.vartool.web.module.VartoolUtils;
import com.vartool.web.module.spring.SpringBeanFactory;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * command component manager
* 
* @fileName	: CommandCmpManager.java
* @author	: ytkim
 */
public class CommandCmpManager implements CmpManager {
	
	private final Map<String, CommandStatus> ALL_LOG_INFO= new ConcurrentHashMap<String, CommandStatus>(); 
	
	private CommandCmpManager() {}
	
	public static CommandCmpManager getInstance() {
		return LogStatusInfoHolder.instance;
	}
	
	private static class LogStatusInfoHolder {
		private static final CommandCmpManager instance = new CommandCmpManager();
	}

	public boolean existsLog(String cmpId) {
		return ALL_LOG_INFO.containsKey(cmpId);
	}
	
	public synchronized void createLogInfo(String cmpId) {
		if(!existsLog(cmpId)) {
			ALL_LOG_INFO.put(cmpId, CommandStatus.builder().logInfo(new LogInfo(1000)).build());
		}
	}
	
	public void addLogInfo(String cmpId, String logText) {
		if(existsLog(cmpId)) {
			ALL_LOG_INFO.get(cmpId).getLogInfo().add(logText);
		}
	}
	
	public String getLogContent(String cmpId) {
		if(existsLog(cmpId)) {
			return ALL_LOG_INFO.get(cmpId).getLogInfo().allLog();
		}
		return "";
	}
	
	public synchronized boolean isRunning(String cmpId) {
		if(existsLog(cmpId)) {
			return ALL_LOG_INFO.get(cmpId).isRunning();
		}
		return false; 
	}
	
	public synchronized void enableCommand(String cmpId, String cmdMode, CommandLogOutputHandler commandLogOutputHandler) {
		if(existsLog(cmpId)) {
			CommandStatus logInfo = ALL_LOG_INFO.get(cmpId);
			logInfo.setCommandLogOutputHandler(commandLogOutputHandler);
			logInfo.setCommandType(CommandType.getCommandType(cmdMode));
			logInfo.setRunning(true);
			logInfo.setUserInfo(SecurityUtils.runningUserInfo());
			
			SpringBeanFactory.getWebSocketService().sendMessage(
				LogMessageDTO.builder().cmpId(cmpId).state(LOG_STATE.START.getCode()).build().addItem("cmd", logInfo.getCommandType().name())
				, VartoolUtils.getCommandRecvId(cmpId)
			);
		}
	}
	
	public synchronized void disableCommand(String cmpId) {
		if(existsLog(cmpId)) {
			ALL_LOG_INFO.get(cmpId).setRunning(false);
			
			SpringBeanFactory.getWebSocketService().sendMessage(
				LogMessageDTO.builder().cmpId(cmpId).state(LOG_STATE.STOP.getCode()).build().addItem("cmd", ALL_LOG_INFO.get(cmpId).getCommandType().name())
				, VartoolUtils.getCommandRecvId(cmpId)
			);
		}
	}
	
	public synchronized void removeLogInfo(String cmpId){
		if(existsLog(cmpId)) {
			disableCommand(cmpId);
			CommandStatus logInfo = ALL_LOG_INFO.get(cmpId);
			logInfo.getCommandLogOutputHandler().stop();
			ALL_LOG_INFO.remove(cmpId);
		}
	}
	
	public synchronized void killCommand(String cmpId){
		if(existsLog(cmpId)) {
			disableCommand(cmpId);
			ALL_LOG_INFO.get(cmpId).getCommandLogOutputHandler().stop();
		}
	}
	
	public synchronized void clearLogInfo(String cmpId) {
		if(existsLog(cmpId)) {
			ALL_LOG_INFO.get(cmpId).getLogInfo().clear();
		}
	}

	public void setLogMonitorData(CmpMonitorDTO lmd) {
		String cmpId = lmd.getCmpId();
		
		if(existsLog(cmpId)) {
			CommandStatus logInfo = ALL_LOG_INFO.get(cmpId);
			
			lmd.setRunning(logInfo.isRunning());
			lmd.setCurrentLogSize(logInfo.getLogInfo().getLogSize());
		}
	}
	
	public Map<String, CommandStatus> getAllLogInfo() {
		return ALL_LOG_INFO; 
	}

	public String currentCmd(String cmpId) {
		if(existsLog(cmpId)) {
			return ALL_LOG_INFO.get(cmpId).getCommandType().name();
		}
		return "";
	}

}

@Getter
@Setter
@ToString
class CommandStatus {
	private LogInfo logInfo;  
	
	private boolean running;
	
	private CommandType commandType;
	
	private Map<String,String> userInfo;
	
	private CommandLogOutputHandler commandLogOutputHandler;
	
	@Builder
	public CommandStatus(LogInfo logInfo) {
		this.logInfo = logInfo;
	}
}