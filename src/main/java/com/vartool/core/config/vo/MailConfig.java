package com.vartool.core.config.vo;

import lombok.Builder;
import lombok.Getter;

/**
 *  mail 설정.
 *  
* @fileName	: MailConfig.java
* @author	: ytkim
 */
@Getter
@Builder
public class MailConfig {
	private boolean enableMail = true;
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
