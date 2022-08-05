package com.vartool.web.constants;

import com.vartech.common.constants.CodeEnumValue;

/**
 * command type
* 
* @fileName	: CommandType.java
* @author	: ytkim
 */
public enum CommandType implements CodeEnumValue {
	START(1)
	,STOP(-1);
	
	private int code; 
	
	CommandType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
	
	public static CommandType getCommandType(String command) {
		return CommandType.valueOf(command.toUpperCase());
	}
}
