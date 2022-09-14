package com.vartool.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadLogInfo {
	
	private String cmpId;
	private String name;
	private String cmpType;
	private String description;
	private String cmpCredential;
	
	private String charset;
	private String logPath;
	private String logType;
	
	private CredentialInfo credentialInfo;
	
	private String remoteHost;
	private int remotePort;
	private String command;
	
	
}