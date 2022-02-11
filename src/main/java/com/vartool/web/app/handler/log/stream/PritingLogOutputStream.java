package com.vartool.web.app.handler.log.stream;

import org.apache.commons.exec.LogOutputStream;

public class PritingLogOutputStream extends LogOutputStream {

	private StringBuilder output = new StringBuilder();
	
	private boolean viewFlag; 
	
	public PritingLogOutputStream(boolean view) {
		this.viewFlag = view; 
	}

	@Override
	protected void processLine(String line, int level) {
		if(viewFlag) {
			System.out.println(line);
		}
		output.append(line).append("\n");
	}

	public String getOutput() {
		return output.toString();
	}
}