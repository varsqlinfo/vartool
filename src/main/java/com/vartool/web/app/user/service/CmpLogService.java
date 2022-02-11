package com.vartool.web.app.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vartech.common.app.beans.ParamMap;
import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.constants.RequestResultCode;
import com.vartool.web.app.handler.log.LogCmpManager;
import com.vartool.web.app.mgmt.component.CmpLogComponent;
import com.vartool.web.dto.response.CmpLogResponseDTO;
import com.vartool.web.dto.websocket.LogMessageDTO;
import com.vartool.web.model.entity.cmp.CmpItemLogEntity;
import com.vartool.web.repository.cmp.CmpItemLogRepository;

/**
 * 
*-----------------------------------------------------------------------------
* @PROJECT	: varsql
* @NAME		: LogItemEventService.java
* @DESC		: log item event service
* @AUTHOR	: ytkim
*-----------------------------------------------------------------------------
  DATE			AUTHOR			DESCRIPTION
*-----------------------------------------------------------------------------
* 2020. 2. 6. 			ytkim			최초작성

*-----------------------------------------------------------------------------
 */
@Component
public class CmpLogService {
	private final static Logger logger = LoggerFactory.getLogger(CmpLogService.class);
	
	@Autowired
	private CmpLogComponent cmpLogComponent;
	
	@Autowired
	private CmpItemLogRepository cmpItemLogRepository;
	
	public ResponseResult loadLog(String cmpId, ParamMap param) {
		
		CmpItemLogEntity logCmp = cmpItemLogRepository.findByCmpId(cmpId);
		
		ResponseResult responseResult = new ResponseResult();
		if(logCmp== null) {
			responseResult.setResultCode(RequestResultCode.NOT_FOUND);
			return responseResult; 
		}
		
		CmpLogResponseDTO dto = CmpLogResponseDTO.toDto(logCmp);
		
		if(!LogCmpManager.getInstance().existsLog(cmpId)) {
			Object result = cmpLogComponent.startAppLog(cmpId, dto);
			if(result != null) {
				responseResult.setItemOne(result);
			}
		}
		
		responseResult.setItemOne(LogMessageDTO.builder().cmpId(cmpId).log(LogCmpManager.getInstance().getLogContent(cmpId)).build());
		
		return responseResult;
	}
}
