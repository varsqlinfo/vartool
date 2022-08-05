package com.vartool.web.app.handler.log.tail;

/**
 * tail output stream
* 
* @fileName	: TailOutputStream.java
* @author	: ytkim
 */
public class TailOutputStream {
	
	private StringBuffer sb = new StringBuffer();
	
	public void handle(String line) {
		sb.append(line);
	}
	
	public String getLog() {
		String result = sb.toString();
		sb.setLength(0);
		return result;
	}
}
