package com.vartool.web.app.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vartech.common.app.beans.ParamMap;
import com.vartech.common.app.beans.ResponseResult;
import com.vartool.web.app.handler.command.CommandCmpManager;
import com.vartool.web.app.mgmt.component.CmpCommandComponent;
import com.vartool.web.dto.response.CmpCommandResponseDTO;
import com.vartool.web.dto.websocket.LogMessageDTO;
import com.vartool.web.model.entity.cmp.CmpItemCommandEntity;
import com.vartool.web.repository.cmp.CmpItemCommandRepository;
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
public class CmpCommandService {
	private final static Logger logger = LoggerFactory.getLogger(CmpCommandService.class);
	
	@Autowired
	private CmpCommandComponent cmpCommandComponent; 
	
	public ResponseResult loadLog(String cmpId, ParamMap param) {
		ResponseResult responseResult = new ResponseResult();
		responseResult.setItemOne(LogMessageDTO.builder().cmpId(cmpId).log(CommandCmpManager.getInstance().getLogContent(cmpId)).build());
		return responseResult;
	}

	public ResponseResult startAndStop(String cmpId, String action, ParamMap allParameter) {
		
		
		return cmpCommandComponent.startAndStop(cmpId, action);
	}
}