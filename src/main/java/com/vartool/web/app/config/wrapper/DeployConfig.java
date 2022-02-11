package com.vartool.web.app.config.wrapper;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class DeployConfig {
	
	@JacksonXmlProperty(localName = "sourcePath")
	private String sourcePath;
	
	@JacksonXmlProperty(localName = "defaultDependencyPath")
	private String defaultDependencyPath;
	
	@JacksonXmlProperty(localName = "antExeFile")
	private String antExeFile;
	
	@JacksonXmlProperty(localName = "gradleExeFile")
	private String gradleExeFile;
	
	@JacksonXmlProperty(localName = "buildFileCreatePath")
	private String buildFileCreatePath;
	
}

