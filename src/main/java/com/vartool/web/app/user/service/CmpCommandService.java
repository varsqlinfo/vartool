package com.vartool.web.app.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vartech.common.app.beans.DataMap;
import com.vartech.common.app.beans.ResponseResult;
import com.vartool.web.app.handler.command.CommandCmpManager;
import com.vartool.web.app.mgmt.component.CmpCommandComponent;
import com.vartool.web.dto.websocket.LogMessageDTO;

/**
 * command service
* 
* @fileName	: CmpCommandService.java
* @author	: ytkim
 */
@Component
public class CmpCommandService {
	private final static Logger logger = LoggerFactory.getLogger(CmpCommandService.class);
	
	@Autowired
	private CmpCommandComponent cmpCommandComponent; 
	
	/**
	 * command log load
	 *
	 * @method : loadLog
	 * @param cmpId
	 * @param param
	 * @return
	 */
	public ResponseResult loadLog(String cmpId, DataMap param) {
		ResponseResult responseResult = new ResponseResult();
		responseResult.setItemOne(LogMessageDTO.builder().cmpId(cmpId).log(CommandCmpManager.getInstance().getLogContent(cmpId)).build());
		return responseResult;
	}
	
	/**
	 * command start and stop
	 *
	 * @method : startAndStop
	 * @param cmpId
	 * @param action
	 * @param allParameter
	 * @return
	 */
	public ResponseResult startAndStop(String cmpId, String action, DataMap allParameter) {
		return cmpCommandComponent.startAndStop(cmpId, action);
	}
}
