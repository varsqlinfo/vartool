package com.vartool.web.app.mgmt.component;

import java.io.File;
import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.constants.RequestResultCode;
import com.vartool.core.config.VartoolConfiguration;
import com.vartool.web.app.handler.command.CommandByteOutputStream;
import com.vartool.web.app.handler.command.CommandCmpManager;
import com.vartool.web.app.handler.command.CommandLogOutputHandler;
import com.vartool.web.app.websocket.service.WebSocketServiceImpl;
import com.vartool.web.constants.AppCode;
import com.vartool.web.constants.VartoolConstants;
import com.vartool.web.dto.response.CmpCommandResponseDTO;
import com.vartool.web.model.entity.cmp.CmpItemCommandEntity;
import com.vartool.web.module.SecurityUtil;
import com.vartool.web.module.Utils;
import com.vartool.web.repository.cmp.CmpItemCommandRepository;

/**
 * 
*-----------------------------------------------------------------------------
* 
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
public class CmpCommandComponent {
	private final static Logger logger = LoggerFactory.getLogger(CmpCommandComponent.class);
	
	@Autowired
	private WebSocketServiceImpl webSocketServiceImpl;
	
	@Autowired
	private CmpItemCommandRepository cmpItemCommandRepository;
	
	/**
	 * 
	 * @Method Name  : startAndStop
	 * @Method 설명 : stop , start 처리.
	 * @작성자   : ytkim
	 * @작성일   : 2020. 2. 6. 
	 * @변경이력  :
	 * @param param
	 * @return
	 */
	public ResponseResult startAndStop(String cmpId, String mode) {
		ResponseResult result = new ResponseResult();
		
		try {
			
			CmpItemCommandEntity cme = cmpItemCommandRepository.findByCmpId(cmpId);
			
			if(cme == null) {
				result.setStatus(RequestResultCode.NOT_FOUND.getCode());
				result.setMessage("item not found");
				result.setItemOne(cmpId);
				return result; 
			}
			
			logger.info("deploy info ip:{}, loginId: {}, action:{},  cmpId: {}, name: {}", SecurityUtil.clientIp(), SecurityUtil.loginName(), mode, cme.getCmpId(), cme.getName());
			
			CmpCommandResponseDTO dto = CmpCommandResponseDTO.toDto(cme);
			
			String startCmd = dto.getStartCmd();
			String stopCmd = dto.getStopCmd();
			
			AppCode.SERVER_CODE resultCode = AppCode.SERVER_CODE.COMPLETE; 
			if("stop".equals(mode)){
				resultCode =commandExecute(dto, mode, stopCmd);
			}else if("start".equals(mode)){
				resultCode =commandExecute(dto, mode, startCmd);
			}else if("stopAndStart".equals(mode)) {
				resultCode =commandExecute(dto, mode, stopCmd);
				resultCode =commandExecute(dto, mode, startCmd);
			}
			
			if(resultCode == AppCode.SERVER_CODE.RUNNING) {
				result.setMessage("already running");
			}
			
		} catch (Exception e) {
			logger.error("ServerService startAndStop {}",e.getMessage(), e);
			result.setMessage(e.getMessage());
		}
		
		return result;
	}
	
	private AppCode.SERVER_CODE commandExecute(CmpCommandResponseDTO dto, String cmdMode, String cmdScript) throws ExecuteException, IOException, InterruptedException {
		
		String cmpId = dto.getCmpId();
		
		if(CommandCmpManager.getInstance().isRunning(cmpId)) {
			return AppCode.SERVER_CODE.RUNNING;
		}
		
		String charset = dto.getCharset();
		
		String tempDir = VartoolConfiguration.getInstance().getConfigInfo().getCommandSavePath();
		File file = new File(tempDir,cmpId+Utils.getOsCommandExtension());
		
		FileUtils.write(file, cmdScript, VartoolConstants.CHAR_SET);
		
		file.setReadable(true);
		file.setWritable(true);
		file.setExecutable(true, false);
		
		logger.debug("tempDir : {} , tmpFileName : {} ", tempDir, cmpId);
		
		String []  cmdArr = Utils.getOsCommand();
		
		CommandLine cmdLine;
		
		if(cmdArr != null) {
			cmdLine = new CommandLine(cmdArr[0]);
			
			for (int i = 1; i < cmdArr.length; i++) {
				cmdLine.addArgument(cmdArr[i]);
			}
			cmdLine.addArgument(file.getAbsolutePath());
		}else {
			cmdLine = new CommandLine(file.getAbsolutePath());
		}
		
		logger.debug("cmdLine : {} " , cmdLine);
		
		ExecuteWatchdog watchdog = new ExecuteWatchdog(60*1000);
		Executor executor = new DefaultExecutor();
		executor.setExitValue(0);
		executor.setWatchdog(watchdog);
	    
		CommandByteOutputStream outLog = new CommandByteOutputStream(charset);
		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
		
		new Thread(new CommandLogOutputHandler(webSocketServiceImpl, dto, cmdMode, watchdog, outLog, 1000)).start();
		
	    PumpStreamHandler pumpStreamHandler = new PumpStreamHandler(outLog, outLog);
	    executor.setStreamHandler(pumpStreamHandler);
	    
	    executor.setExitValue(0);
		executor.execute(cmdLine, resultHandler);
		
		//resultHandler.waitFor();
		
		return AppCode.SERVER_CODE.COMPLETE;
		
	}
}
