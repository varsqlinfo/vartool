package com.vartool.core.config.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *  mail 설정.
 *  
* @fileName	: MailConfig.java
* @author	: ytkim
 */
@Getter
@NoArgsConstructor
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
	
	@Builder
	public MailConfig(boolean enableMail, String host, int port, String username, String password, String fromEmail, boolean smtpAuth, boolean starttlsEnable, String transportProtocol, boolean debug ) {
		this.enableMail = enableMail;
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.fromEmail = fromEmail;
		this.smtpAuth = smtpAuth;
		this.starttlsEnable = starttlsEnable;
		this.transportProtocol = transportProtocol;
		this.debug = debug; 
	}
}
