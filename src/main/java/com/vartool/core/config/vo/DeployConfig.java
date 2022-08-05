package com.vartool.core.config.vo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DeployConfig {
	private String sourcePath;
	private String defaultDependencyPath;
	private String antExeFile;
	private String gradleExeFile;
	private String buildFileCreatePath;
}
