package com.vartool.core.config.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * deploy 설정
 * 
* @fileName	: DeployConfig.java
* @author	: ytkim
 */
@Setter
@Getter
public class DeployConfig {
	private String sourcePath;
	private String defaultDependencyPath;
	private String antExeFile;
	private String gradleExeFile;
	private String buildFileCreatePath;
}
