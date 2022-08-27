package com.vartool.core.config.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * App config
 * 
* @fileName	: AppConfig.java
* @author	: ytkim
 */
@Setter
@Getter
public class AppConfig {
	private String charset;
	private String javaHome;
	private String commandSavePath;
	private String uploadPath;
	private String osType;
	
	private String protocol;
	private String hostname;
	private int port;
	private String contextPath;
	
	private String schedulerEnable;
	
	private long fileUploadSize;
	private long fileUploadSizePerFile;
	private int fileUploadMaxInMemorySize;
	
	private DbConfig db; 
	private PasswordConfig passwordConfig; 
	private UserPassowrdResetConfig userPassowrdResetConfig; 
	private MailConfig mail; 
	private DeployConfig deploy;
}
