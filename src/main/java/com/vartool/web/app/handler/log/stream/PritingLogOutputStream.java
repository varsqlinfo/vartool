package com.vartool.web.app.handler.log.stream;

import org.apache.commons.exec.LogOutputStream;

import com.vartool.web.constants.BlankConstants;

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
		output.append(line).append(BlankConstants.NEW_LINE_CHAR);
	}

	public String getOutput() {
		return output.toString();
	}
}