package com.vartool.web.app.handler.log.stream;

import org.apache.commons.exec.LogOutputStream;

import com.vartool.web.constants.BlankConstants;

/**
 * log component output stream
* 
* @fileName	: LogComponentOutputStream.java
* @author	: ytkim
 */
public class LogComponentOutputStream extends LogOutputStream {
	
	private boolean addNewLineChar;   
	
	public LogComponentOutputStream() {
		this(false);
	}
	public LogComponentOutputStream(boolean addNewLineChar) {
		this.addNewLineChar = addNewLineChar; 
	}
	
	private StringBuffer sb = new StringBuffer();
	
	public String getLog() {
		String result = sb.toString();
		sb.setLength(0);
		return result;
	}
	
	public void processLine(String line) {
		processLine(line, getMessageLevel());
	}

	@Override
	protected void processLine(String line, int logLevel) {
		sb.append(line);
		
		if(this.addNewLineChar) {
			sb.append(BlankConstants.NEW_LINE);
		}
		
	}
}
