package com.vartool.web.app.user.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.jknack.handlebars.internal.lang3.StringUtils;
import com.vartech.common.app.beans.DataMap;
import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.constants.RequestResultCode;
import com.vartool.web.app.handler.log.LogCmpManager;
import com.vartool.web.app.mgmt.component.CmpLogComponent;
import com.vartool.web.app.mgmt.service.CredentialsProviderMgmtService;
import com.vartool.web.constants.BlankConstants;
import com.vartool.web.constants.LogType;
import com.vartool.web.constants.VartoolConstants;
import com.vartool.web.dto.ReadLogInfo;
import com.vartool.web.dto.websocket.LogMessageDTO;
import com.vartool.web.module.FileServiceUtils;
import com.vartool.web.repository.cmp.CmpItemLogRepository;

import lombok.RequiredArgsConstructor;

/**
 * log service
* 
* @fileName	: CmpLogService.java
* @author	: ytkim
 */
@Component
@RequiredArgsConstructor
public class CmpLogService {
	private final static Logger logger = LoggerFactory.getLogger(CmpLogService.class);
	
	final private CmpLogComponent cmpLogComponent;
	
	final private CmpItemLogRepository cmpItemLogRepository;
	
	final private CredentialsProviderMgmtService credentialsProviderMgmtService;
	
	/**
	 * log load
	 *
	 * @method : loadLog
	 * @param cmpId
	 * @param param
	 * @return
	 */
	public ResponseResult loadLog(String cmpId, DataMap param) {
		
		ReadLogInfo readLogInfo = cmpItemLogRepository.findReadLogInfo(cmpId);
		
		
		if(readLogInfo== null) {
			return ResponseResult.builder().resultCode(RequestResultCode.NOT_FOUND).build(); 
		}
		
		if(LogCmpManager.getInstance().existsLog(cmpId)) { // log 실행중일때. 
			return ResponseResult.builder().item(LogMessageDTO.builder().cmpId(cmpId).log(LogCmpManager.getInstance().getLogContent(cmpId)).build()).build();
		}
		
		LogMessageDTO logInfo = cmpLogComponent.startLog(cmpId, readLogInfo);
		
		if(logInfo == null && LogType.FILE.name().equals(readLogInfo.getLogType())) {
			logInfo = loadFileLog(readLogInfo);
		}
		
		return ResponseResult.builder().item(logInfo).build();
	}
	
	
	/**
	 * file log load
	 *
	 * @method : loadFileLog
	 * @param dto
	 * @param responseResult
	 * @return 
	 */
	private LogMessageDTO loadFileLog(ReadLogInfo dto) {
		
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
					
					return LogMessageDTO.builder().cmpId(dto.getCmpId()).log(sb.toString()).build(); 
				}
			} catch (IOException e) {
				logger.error("log load fail logPath : {}, error message : {}", dto.getLogPath() , e.getMessage());
			}
		}
		
		return null; 
	}
}
