package com.vartool.web.app.mgmt.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vartech.common.app.beans.ParamMap;
import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.utils.VartechUtils;
import com.vartool.web.app.handler.command.CommandCmpManager;
import com.vartool.web.app.handler.deploy.DeployCmpManager;
import com.vartool.web.app.handler.log.LogCmpManager;
import com.vartool.web.app.handler.log.tail.TailLogOutputHandler;
import com.vartool.web.app.websocket.service.WebSocketServiceImpl;
import com.vartool.web.constants.ComponentConstants;
import com.vartool.web.constants.ResourceConfigConstants;
import com.vartool.web.dto.response.CmpLogResponseDTO;
import com.vartool.web.dto.response.CmpMonitorDTO;
import com.vartool.web.dto.response.CmpResponseDTO;
import com.vartool.web.dto.websocket.LogMessageDTO;
import com.vartool.web.model.entity.cmp.CmpEntity;
import com.vartool.web.module.VartoolUtils;
import com.vartool.web.repository.cmp.CmpItemLogRepository;
import com.vartool.web.repository.cmp.CmpRepository;

/**
 * 
*-----------------------------------------------------------------------------
* @PROJECT	: varsql
* @NAME		: LogInfoService.java
* @DESC		: 설정 정보 처리. 
* @AUTHOR	: ytkim
*-----------------------------------------------------------------------------
  DATE			AUTHOR			DESCRIPTION
*-----------------------------------------------------------------------------
* 2020. 2. 6. 			ytkim			최초작성

*-----------------------------------------------------------------------------
 */
@Component
public class CmpMonitoringService {
	private final static Logger logger = LoggerFactory.getLogger(CmpMonitoringService.class);
	
	@Autowired
	@Qualifier(ResourceConfigConstants.APP_MODEL_MAPPER)
	private ModelMapper modelMapper;
	
	@Autowired
	private CmpItemLogRepository cmpItemLogRepository;
	
	@Autowired
	private CmpRepository cmpRepository;
	
	@Autowired
	private WebSocketServiceImpl webSocketServiceImpl;
	
	/**
	 * 
	 * @Method Name  : logInfoList
	 * @Method 설명 : 컴포넌트 목록 보기. 
	 * @작성자   : ytkim
	 * @작성일   : 2021. 8. 12.
	 * @변경이력  :
	 * @param allParameter
	 * @return
	 */
	public ResponseResult logInfoList(ParamMap allParameter) {
		
		List<CmpResponseDTO> cmpItemList = cmpRepository.findAllByNameContaining(Sort.by(Sort.Direction.ASC, CmpEntity.CMP_TYPE).and(Sort.by(Sort.Direction.ASC, CmpEntity.NAME)));
		
		List<CmpMonitorDTO> monitorList = new ArrayList<CmpMonitorDTO>();
		for (CmpResponseDTO item : cmpItemList) {
			
			CmpMonitorDTO lmd = CmpMonitorDTO.toDto(item);
			
			ComponentConstants.TYPE cmpType = ComponentConstants.getComponentType(lmd.getCmpType()); 
			
			if(ComponentConstants.TYPE.COMMAND.equals(cmpType)) {
				CommandCmpManager.getInstance().setLogMonitorData(lmd);
			}else if(ComponentConstants.TYPE.DEPLOY.equals(cmpType)) {
				DeployCmpManager.getInstance().setLogMonitorData(lmd);
			}else if(ComponentConstants.TYPE.LOG.equals(cmpType)) {
				LogCmpManager.getInstance().setLogMonitorData(lmd);
			}
			monitorList.add(lmd);
		}
		
		ResponseResult result  = new ResponseResult();
		result.setItemList(monitorList);
		return result;
	}
	
	/**
	 * 
	 * @Method Name  : checkComponent
	 * @Method 설명 : check compoent
	 * @작성자   : ytkim
	 * @작성일   : 2021. 8. 12.
	 * @변경이력  :
	 * @param cmpInfo
	 * @param allParameter
	 * @return
	 */
	public ResponseResult checkComponent(String cmpInfo, ParamMap allParameter) {
		TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};
		HashMap<String,String> cmpInfoMap= VartechUtils.jsonStringToObject(cmpInfo, typeRef);
		
		List<CmpMonitorDTO> monitorList = new ArrayList<CmpMonitorDTO>();
		
		for(Entry<String, String> entry : cmpInfoMap.entrySet()) {
			CmpMonitorDTO lmd = new CmpMonitorDTO();
			lmd.setCmpId(entry.getKey());
			lmd.setCmpType(entry.getValue());
			ComponentConstants.TYPE cmpType = ComponentConstants.getComponentType(lmd.getCmpType());
			
			if(ComponentConstants.TYPE.COMMAND.equals(cmpType)) {
				CommandCmpManager.getInstance().setLogMonitorData(lmd);
			}else if(ComponentConstants.TYPE.DEPLOY.equals(cmpType)) {
				DeployCmpManager.getInstance().setLogMonitorData(lmd);
			}else if(ComponentConstants.TYPE.LOG.equals(cmpType)) {
				LogCmpManager.getInstance().setLogMonitorData(lmd);
			}
			monitorList.add(lmd); 
		}
		
		ResponseResult result  = new ResponseResult();
		result.setItemList(monitorList);
		return result;
	}
	
	/**
	 * 
	 * @Method Name  : clearLog
	 * @Method 설명 : 로그 클리어.  
	 * @작성자   : ytkim
	 * @작성일   : 2021. 4. 9.
	 * @변경이력  :
	 * @param cmpId
	 * @param logItemUid
	 * @param allParameter
	 * @return
	 */
	public ResponseResult clearLog(String cmpId, String type) {
		
		ComponentConstants.TYPE cmpType = ComponentConstants.getComponentType(type); 
		
		if(ComponentConstants.TYPE.COMMAND.equals(cmpType)) {
			CommandCmpManager.getInstance().clearLogInfo(cmpId);
		}else if(ComponentConstants.TYPE.DEPLOY.equals(cmpType)) {
			DeployCmpManager.getInstance().clearLogInfo(cmpId);
		}else if(ComponentConstants.TYPE.LOG.equals(cmpType)) {
			LogCmpManager.getInstance().clearLogInfo(cmpId);
		}
		
		return VartoolUtils.getResponseResultItemOne(1);
	}
	
	/**
	 * 
	 * @Method Name  : startTail
	 * @Method 설명 : start tail
	 * @작성자   : ytkim
	 * @작성일   : 2020. 2. 9.
	 * @변경이력  :
	 * @param uid
	 * @param groupId
	 * @param allParameter
	 * @return
	 */
	public ResponseResult startTail(String cmpId, ParamMap param) {
		if(LogCmpManager.getInstance().isTailInfo(cmpId)) {
			return VartoolUtils.getResponseResultItemOne("running");
		}else {
			CmpLogResponseDTO dto = CmpLogResponseDTO.toDto(cmpItemLogRepository.findByCmpId(cmpId));
			Object result = startAppLog( cmpId, dto);
			return VartoolUtils.getResponseResultItemOne(result);
		}
	}
	
	/**
	 * 
	 * @Method Name  : stopTail
	 * @Method 설명 : tail stop
	 * @작성자   : ytkim
	 * @작성일   : 2020. 2. 9.
	 * @변경이력  :
	 * @param uid
	 * @param logItemUid
	 * @param allParameter
	 * @return
	 */
	public ResponseResult stopTail(String cmpId) {
		LogCmpManager.getInstance().stopTail(cmpId);
		return VartoolUtils.getResponseResultItemOne(1);
	}

	public ResponseResult killProcess(String cmpId) {
		CommandCmpManager.getInstance().killCommand(cmpId);
		return VartoolUtils.getResponseResultItemOne(1);
	}
	
	public Object startAppLog(String cmpId, CmpLogResponseDTO dto) {
		
		String logPath = dto.getLogPath(); 
		if(!StringUtils.isBlank(logPath) && new File(logPath).exists()) {
			new Thread(new TailLogOutputHandler(webSocketServiceImpl, dto, 1000)).start();
		}else {
			return LogMessageDTO.builder().cmpId(cmpId).log("log path not found : " + logPath).build();
		}
		
		return null;
	}
}
