package com.vartool.web.app.user.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.jknack.handlebars.internal.lang3.StringUtils;
import com.vartech.common.app.beans.DataMap;
import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.constants.RequestResultCode;
import com.vartool.web.app.handler.log.LogCmpManager;
import com.vartool.web.app.mgmt.component.CmpLogComponent;
import com.vartool.web.constants.BlankConstants;
import com.vartool.web.constants.VartoolConstants;
import com.vartool.web.dto.response.CmpLogResponseDTO;
import com.vartool.web.dto.websocket.LogMessageDTO;
import com.vartool.web.model.entity.cmp.CmpItemLogEntity;
import com.vartool.web.module.FileServiceUtils;
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
	
	public ResponseResult loadLog(String cmpId, DataMap param) {
		
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
				return responseResult; 
			}else {
				File file = FileServiceUtils.logFile(dto.getLogPath());
				
				if(file.exists()) {
					String charset = dto.getCharset();
					charset = StringUtils.isBlank(charset) ? VartoolConstants.CHAR_SET : charset;
					try (ReversedLinesFileReader  reader = new ReversedLinesFileReader(file, Charset.forName(charset))){
						
						List<String> readLogs = reader.readLines(1000);
						
						if(readLogs.size() > 0) {
							StringBuilder sb = new StringBuilder();
							
							readLogs.forEach(logCont ->{
								sb.append(logCont).append(BlankConstants.NEW_LINE_CHAR);
							});
							responseResult.setItemOne(LogMessageDTO.builder().cmpId(cmpId).log(sb.toString()).build());
						}
						return responseResult; 
					} catch (IOException e) {
						logger.error("log load fail logPath : {}, error message : {}", dto.getLogPath() , e.getMessage());
					}
				}
			}
		}
		
		responseResult.setItemOne(LogMessageDTO.builder().cmpId(cmpId).log(LogCmpManager.getInstance().getLogContent(cmpId)).build());
		
		return responseResult;
	}
}
