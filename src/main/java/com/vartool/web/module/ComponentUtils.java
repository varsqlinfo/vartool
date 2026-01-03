package com.vartool.web.module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vartech.common.utils.StringUtils;
import com.vartool.core.config.VartoolConfiguration;
import com.vartool.web.app.websocket.service.WebSocketServiceImpl;
import com.vartool.web.configuration.websocket.WebSocketBrokerConfig;
import com.vartool.web.constants.TemplateConvertKeyCode;
import com.vartool.web.dto.DeployInfo;
import com.vartool.web.dto.websocket.LogMessageDTO;
import com.vartool.web.template.DeploySourceReplaceUtils;

/**
 * component util
 * 
 * @author ytkim
 *
 */
public final class ComponentUtils {
	

	private ComponentUtils() {
	}

	public static String getBuildScript(DeployInfo dto) throws IOException {
		String buildScript = dto.getBuildScript();
			
		buildScript = buildScript.trim();
		
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
		
		return buildScript; 
	}
}
