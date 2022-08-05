package com.vartool.core.config.vo;

import lombok.Getter;

/**
 * db conection config
 * 
* @fileName	: DbConfig.java
* @author	: ytkim
 */
@Getter
public class DbConfig {
	private String type;
	private String url;
	private String username;
	private String password;
	private String driverClass;
}
