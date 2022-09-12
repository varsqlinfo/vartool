package com.vartool.web.constants;

/**
 * log type
* 
* @fileName	: LogType.java
* @author	: ytkim
 */
public enum CredentialsType {
	USERNAME_PASSWORD("idPw","Username & Password")
	,SECRET_TEXT("secretText","Secret Text");

	private String code;
	private String viewLabel;
	
	
	CredentialsType(String code, String viewLabel) {
		this.code = code; 
		this.viewLabel = viewLabel; 
	}

	public String getName() {
		return this.name();
	}
	
	public static CredentialsType getLogType(String code) {
		
		for (CredentialsType credentialsType : values()) {
			if(credentialsType.getCode().equals(code)) {
				return credentialsType; 
			}
		}
		
		return null;
	}

	public String getCode() {
		return code;
	}

	public String getViewLabel() {
		return viewLabel;
	}
}
