package com.vartool.web.app.config.vo;

import lombok.Getter;

@Getter
public class MailConfig {
	private String host;
	private int port;
	private String username;
	private String password;
	private String fromEmail;
	private boolean smtpAuth;
	private boolean starttlsEnable; 
	private String transportProtocol;
	private boolean debug; 
}
