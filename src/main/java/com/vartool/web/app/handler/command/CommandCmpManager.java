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

	public boolean existsLog(String uid) {
		return ALL_LOG_INFO.containsKey(uid);
	}
	
	public synchronized void createLogInfo(String uid) {
		if(!existsLog(uid)) {
			ALL_LOG_INFO.put(uid, CommandStatus.builder().logInfo(new LogInfo(1000)).build());
		}
	}
	
	public void addLogInfo(String uid, String logText) {
		if(existsLog(uid)) {
			ALL_LOG_INFO.get(uid).getLogInfo().add(logText);
		}
	}
	
	public String getLogContent(String uid) {
		if(existsLog(uid)) {
			return ALL_LOG_INFO.get(uid).getLogInfo().allLog();
		}
		return "";
	}
	
	public synchronized boolean isRunning(String uid) {
		if(existsLog(uid)) {
			return ALL_LOG_INFO.get(uid).isRunning();
		}
		return false; 
	}
	
	public synchronized void enableCommand(String uid, String cmdMode, CommandLogOutputHandler commandLogOutputHandler) {
		if(existsLog(uid)) {
			CommandStatus logInfo = ALL_LOG_INFO.get(uid);
			logInfo.setCommandLogOutputHandler(commandLogOutputHandler);
			logInfo.setCommandType(CommandType.getCommandType(cmdMode));
			logInfo.setRunning(true);
			logInfo.setUserInfo(SecurityUtils.runningUserInfo());
			
			SpringBeanFactory.getWebSocketService().sendMessage(
				LogMessageDTO.builder().cmpId(uid).state(LOG_STATE.START.getCode()).build().addItem("cmd", logInfo.getCommandType().name())
				, VartoolUtils.getCommandRecvId(uid)
			);
		}
	}
	
	public synchronized void disableCommand(String uid) {
		if(existsLog(uid)) {
			ALL_LOG_INFO.get(uid).setRunning(false);
			
			SpringBeanFactory.getWebSocketService().sendMessage(
				LogMessageDTO.builder().cmpId(uid).state(LOG_STATE.STOP.getCode()).build().addItem("cmd", ALL_LOG_INFO.get(uid).getCommandType().name())
				, VartoolUtils.getCommandRecvId(uid)
			);
		}
	}
	
	public synchronized void removeLogInfo(String uid){
		if(existsLog(uid)) {
			disableCommand(uid);
			CommandStatus logInfo = ALL_LOG_INFO.get(uid);
			logInfo.getCommandLogOutputHandler().stop();
			ALL_LOG_INFO.remove(uid);
		}
	}
	
	public synchronized void killCommand(String uid){
		if(existsLog(uid)) {
			disableCommand(uid);
			ALL_LOG_INFO.get(uid).getCommandLogOutputHandler().stop();
		}
	}
	
	public synchronized void clearLogInfo(String uid) {
		if(existsLog(uid)) {
			ALL_LOG_INFO.get(uid).getLogInfo().clear();
		}
	}

	public void setLogMonitorData(CmpMonitorDTO lmd) {
		String uid = lmd.getCmpId();
		
		if(existsLog(uid)) {
			CommandStatus logInfo = ALL_LOG_INFO.get(uid);
			
			lmd.setRunning(logInfo.isRunning());
			lmd.setCurrentLogSize(logInfo.getLogInfo().getLogSize());
		}
	}
	
	public Map<String, CommandStatus> getAllLogInfo() {
		return ALL_LOG_INFO; 
	}

	public String currentCmd(String uid) {
		if(existsLog(uid)) {
			return ALL_LOG_INFO.get(uid).getCommandType().name();
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