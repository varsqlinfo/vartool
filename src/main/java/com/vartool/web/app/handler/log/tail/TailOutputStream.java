package com.vartool.web.app.handler.log.tail;
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
