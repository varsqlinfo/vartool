package com.vartool.web.constants;

/**
 * log type
* 
* @fileName	: LogType.java
* @author	: ytkim
 */
public enum LogType {
	FILE
	,SSH;
	//,TELNET;
	
	
	public String getName() {
		return this.name();
	}
	
	public static LogType getLogType(String command) {
		return LogType.valueOf(command.toUpperCase());
	}
}
