package com.vartool.web.app.config.wrapper;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@JacksonXmlRootElement(localName = "vartool-config")
public class VartoolConfig {
	@JacksonXmlProperty(localName = "javaHome")
	private String javaHome;
	
	@JacksonXmlProperty(localName = "osType")
	private String osType;
	
	@JacksonXmlProperty(localName = "charset")
	private String charset;
	
	@JacksonXmlProperty(localName = "commandSavePath")
	private String commandSavePath;
	
	@JacksonXmlProperty(localName = "fileUploadSize")
	private long fileUploadSize = -1;
	
	@JacksonXmlProperty(localName = "fileUploadSizePerFile")
	private long fileUploadSizePerFile = -1;
	
	@JacksonXmlProperty(localName = "fileUploadMaxInMemorySize")
	private int fileUploadMaxInMemorySize = 0;
	
	@JacksonXmlProperty(localName = "deploy-config")
	private DeployConfig deployConfig;
	
	@JacksonXmlProperty(localName = "uploadPath")
	private String uploadPath;
}

