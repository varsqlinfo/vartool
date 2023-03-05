package com.vartool.web.app.handler.deploy;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.vartool.web.app.handler.CmpManager;
import com.vartool.web.constants.AppCode.LOG_STATE;
import com.vartool.web.constants.CommandType;
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
 * deploy component manager
* 
* @fileName	: DeployCmpManager.java
* @author	: ytkim
 */
public class DeployCmpManager implements CmpManager {
	
	private final Map<String, DeployStatus> ALL_LOG_INFO= new ConcurrentHashMap<String, DeployStatus>(); 
	
	private DeployCmpManager() {}
	
	public static DeployCmpManager getInstance() {
		return LogStatusInfoHolder.instance;
	}
	
	private static class LogStatusInfoHolder {
		private static final DeployCmpManager instance = new DeployCmpManager();
	}

	public boolean existsLog(String deployUid) {
		return ALL_LOG_INFO.containsKey(deployUid);
	}
	
	public synchronized void createLogInfo(String deployUid) {
		if(!existsLog(deployUid)) {
			ALL_LOG_INFO.put(deployUid, DeployStatus.builder().logInfo(new LogInfo(500)).build());
		}
	}
	
	public void addLogInfo(String deployUid, Deque<String> logText) {
		if(existsLog(deployUid)) {
			ALL_LOG_INFO.get(deployUid).getLogInfo().add(logText);
		}
	}
	
	public Collection<String> getLogContent(String deployUid) {
		if(existsLog(deployUid)) {
			return ALL_LOG_INFO.get(deployUid).getLogInfo().allLog();
		}
		return Collections.EMPTY_LIST;
	}
	
	public synchronized boolean isRunning(String deployUid) {
		if(existsLog(deployUid)) {
			return ALL_LOG_INFO.get(deployUid).isRunning();
		}
		return false; 
	}
	
	public synchronized void enable(String deployUid) {
		if(existsLog(deployUid)) {
			DeployStatus logInfo = ALL_LOG_INFO.get(deployUid);
			logInfo.setRunning(true);
			logInfo.setUserInfo(SecurityUtils.runningUserInfo());
			
			SpringBeanFactory.getWebSocketService().sendMessage(
				getRunningUserInfo(LogMessageDTO.builder().cmpId(deployUid).state(LOG_STATE.START.getCode()).build())
				, VartoolUtils.getDeployRecvId(deployUid)
			);
		}
	}
	
	public synchronized void disable(String deployUid) {
		if(existsLog(deployUid)) {
			ALL_LOG_INFO.get(deployUid).setRunning(false);
			
			SpringBeanFactory.getWebSocketService().sendMessage(
				getRunningUserInfo(LogMessageDTO.builder().cmpId(deployUid).state(LOG_STATE.STOP.getCode()).build())
				, VartoolUtils.getDeployRecvId(deployUid)
			);
		}
	}
	
	public synchronized void removeLogInfo(String deployUid){
		if(existsLog(deployUid)) {
			disable(deployUid);
			ALL_LOG_INFO.remove(deployUid);
		}
	}
	
	public synchronized void clearLogInfo(String deployUid) {
		if(existsLog(deployUid)) {
			ALL_LOG_INFO.get(deployUid).getLogInfo().clear();
		}
	}

	public void setLogMonitorData(CmpMonitorDTO lmd) {
		String cmpId = lmd.getCmpId();
		
		if(existsLog(cmpId)) {
			DeployStatus logInfo = ALL_LOG_INFO.get(cmpId);
			
			lmd.setRunning(logInfo.isRunning());
			lmd.setCurrentLogSize(logInfo.getLogInfo().getLogSize());
		}
	}
	
	public Map<String, DeployStatus> getAllLogInfo() {
		return ALL_LOG_INFO; 
	}
	
	/**
	 * 
	 * @Method Name  : getRunningUserInfo
	 * @Method 설명 : 최종 실행한 사용자 정보. 
	 * @작성자   : ytkim
	 * @작성일   : 2021. 4. 22.
	 * @변경이력  :
	 * @param deployUid
	 * @return
	 */
	public Object getRunningUserInfo(String deployUid) {
		if(existsLog(deployUid)) {
			return ALL_LOG_INFO.get(deployUid).getUserInfo();
		}
		
		return null;
	}
	
	public LogMessageDTO getRunningUserInfo(LogMessageDTO lmd) {
		String deployUid = lmd.getCmpId(); 
		if(existsLog(deployUid)) {
			return VartoolUtils.setRunningUserInfo(lmd, ALL_LOG_INFO.get(deployUid).getUserInfo());
		}
		return lmd;
	}

}


@Getter
@Setter
@ToString
class DeployStatus {
	private LogInfo logInfo;  
	
	private boolean running;
	
	private CommandType commandType;
	
	private Map<String,String> userInfo;
	
	@Builder
	public DeployStatus(LogInfo logInfo) {
		this.logInfo = logInfo;
	}
}
