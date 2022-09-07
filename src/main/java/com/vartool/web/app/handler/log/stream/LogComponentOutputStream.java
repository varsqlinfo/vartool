package com.vartool.web.app.handler.log.stream;

import org.apache.commons.exec.LogOutputStream;

/**
 * log component output stream
* 
* @fileName	: LogComponentOutputStream.java
* @author	: ytkim
 */
public class LogComponentOutputStream extends LogOutputStream {
	
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
		
	}
}
