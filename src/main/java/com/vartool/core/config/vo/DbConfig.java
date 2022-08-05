package com.vartool.core.config.vo;

import lombok.Getter;

@Getter
public class DbConfig {
	private String type;
	private String url;
	private String username;
	private String password;
	private String driverClass;
}
