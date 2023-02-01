package com.vartool.web.constants;

/**
 * mail type
* 
* @fileName	: MailType.java
* @author	: ytkim
 */
public enum MailType {
	RESET_PASSWORD("reset_pw"), INFO("info"), REGISTER("register");

	private String messageType;

	MailType(String msgType) {
		this.messageType = msgType;
	}

	public String getType() {
		return messageType;
	}

}