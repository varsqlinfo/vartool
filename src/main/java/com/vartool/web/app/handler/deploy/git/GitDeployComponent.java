package com.vartool.web.app.handler.deploy.git;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.utils.StringUtils;
import com.vartool.core.config.VartoolConfiguration;
import com.vartool.web.app.handler.deploy.AbstractDeploy;
import com.vartool.web.app.handler.deploy.DeployCmpManager;
import com.vartool.web.app.websocket.service.WebSocketServiceImpl;
import com.vartool.web.constants.TemplateConvertKeyCode;
import com.vartool.web.constants.VartoolConstants;
import com.vartool.web.dto.DeployInfo;
import com.vartool.web.dto.websocket.LogMessageDTO;
import com.vartool.web.module.LogFilenameUtils;
import com.vartool.web.module.VartoolUtils;
import com.vartool.web.template.DeploySourceReplaceUtils;

/**
 * git deploy component
* 
* @fileName	: GitDeployComponent.java
* @author	: ytkim
 */
@Component
public class GitDeployComponent extends AbstractDeploy{
	
	private final static Logger logger = LoggerFactory.getLogger(GitDeployComponent.class);
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:sss");
	
	public GitDeployComponent(WebSocketServiceImpl webSocketServiceImpl) {
		super(webSocketServiceImpl);
	}
	
	@Override
	public ResponseResult deployAction(DeployInfo dto) {
		ResponseResult result = new ResponseResult();
		
		String cmpId = dto.getCmpId();
		
		LogMessageDTO msgData = LogMessageDTO.builder().cmpId(cmpId).build();
		
		String recvId = VartoolUtils.getDeployRecvId(cmpId);
		
		try{
			String actionMode =dto.getAction(); 
			
			DeployCmpManager.getInstance().createLogInfo(cmpId);
			
			DeployCmpManager.getInstance().enable(cmpId);
			
			msgData.setLog(dateFormat.format(System.currentTimeMillis())+"-==deploy start==-");
			
			sendLogMessage( msgData, recvId);
			
			if("pull".equals(actionMode) || "all".equals(actionMode)){
				sourcePull(cmpId, dto, msgData, recvId, "pull".equals(actionMode));
			}
			
			if("deploy".equals(actionMode)||"all".equals(actionMode)){
				String buildScript = dto.getBuildScript();
				
				buildScript = buildScript.trim();
				
				String createPath = VartoolConfiguration.getInstance().getDeployConfig().getBuildFileCreatePath();
				String antExeFile = VartoolConfiguration.getInstance().getDeployConfig().getAntExeFile();
				
				String outputBuildFile = createPath + File.separator +  cmpId+"build.xml"; 
				
				ObjectMapper mapper = new ObjectMapper();
				
				Map replaceInfo = mapper.convertValue(dto, Map.class);
				
				String dependencyPath = dto.getDependencyPath();
				
				if(StringUtils.isBlank(dependencyPath)) {
					dependencyPath = VartoolConfiguration.getInstance().getDeployConfig().getDefaultDependencyPath();
				}
				
				replaceInfo.put(TemplateConvertKeyCode.DEPLOY.DEFAULT_DEPENDENCY_PATH.getKey(), dependencyPath);
				replaceInfo.put(TemplateConvertKeyCode.DEPLOY.SOURCE_PATH.getKey(), LogFilenameUtils.getDeploySourcePath(dto).getAbsolutePath());
				replaceInfo.put(TemplateConvertKeyCode.DEPLOY.BUILD_PATH.getKey(), LogFilenameUtils.getDeployBuildPath(dto).getAbsolutePath());
				
				buildScript  = DeploySourceReplaceUtils.getInstance().getBuildSource(buildScript, replaceInfo);
				
				FileUtils.write(new File(outputBuildFile), buildScript, VartoolConstants.CHAR_SET);
				
				logger.debug("deploy ant xml output file : {}", outputBuildFile);
				// build path 
//					msgData.put("log", "buildPath create : "+outputBuildFile);
//					webSocketServiceImpl.sendData(msgData);
				new CommandExeCtrl(this, recvId, LogMessageDTO.builder().cmpId(cmpId).build()).execAndRtnResult(recvId ,antExeFile, outputBuildFile);
			}
			
			msgData.setLog(dateFormat.format(System.currentTimeMillis())+"-==deploy end==-");
			
			sendLogMessage(msgData, recvId);
		}catch(Exception e){
			msgData.setLog(dateFormat.format(System.currentTimeMillis())+"-==deploy error==-"+ e.getMessage());
			
			sendLogMessage( msgData, recvId);
			logger.error("deploy : {}", e.getMessage(), e);
			
		}finally{
			DeployCmpManager.getInstance().disable(cmpId);
		}
		
		return result;
	}

	@Override
	public ResponseResult autoDeploy(DeployInfo dto) {
		// TODO Auto-generated method stub
		return null;
	}


	private boolean sourcePull(String entryUniqueId, DeployInfo dto, LogMessageDTO msgData, String recvId, boolean onlyPull) {
		
		GitSource gitSource =new GitSource(this, msgData, recvId);
		boolean gitSourceFlag = false; 
		try{
			gitSource.gitPull(dto, onlyPull);
			gitSourceFlag = true; 
		}catch(Exception e){
			logger.error("gitAction", e);
			msgData.setLog(dateFormat.format(System.currentTimeMillis())+" : "+ e.getMessage());
			sendLogMessage(msgData, recvId);
			gitSourceFlag =false; 
		}finally{
			gitSource.close();
		}
		
		return gitSourceFlag; 
		
	}
}
