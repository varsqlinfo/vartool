package com.vartool.web.app.handler.deploy.git;

import org.eclipse.jgit.lib.BatchingProgressMonitor;

import com.vartool.web.app.handler.deploy.AbstractDeploy;
import com.vartool.web.constants.BlankConstants;
import com.vartool.web.dto.websocket.LogMessageDTO;

public class GitProgressMoniter extends BatchingProgressMonitor {
	
	private AbstractDeploy deployAbstract;
	private LogMessageDTO logMessageDto = null; 
	private String recvId; 
	
	public GitProgressMoniter(AbstractDeploy deployAbstract, LogMessageDTO msgData, String recvId) {
		this.deployAbstract = deployAbstract; 
		this.logMessageDto = msgData;
		this.recvId = recvId;
	}

	protected void onUpdate(String taskName, int workCurr) {
		StringBuilder s = new StringBuilder();
		format(s, taskName, workCurr);
		send(s);
	}

	protected void onEndTask(String taskName, int workCurr) {
		StringBuilder s = new StringBuilder();
		format(s, taskName, workCurr);
		s.append(BlankConstants.NEW_LINE_CHAR);
		send(s);
	}

	private void format(StringBuilder s, String taskName, int workCurr) {
		s.append(taskName);
		s.append(": ");
		while (s.length() < 25) {
			s.append(' ');
		}
		s.append(workCurr);
	}

	protected void onUpdate(String taskName, int cmp, int totalWork, int pcnt) {
		StringBuilder s = new StringBuilder();
		format(s, taskName, cmp, totalWork, pcnt);
		send(s);
	}

	protected void onEndTask(String taskName, int cmp, int totalWork, int pcnt) {
		StringBuilder s = new StringBuilder();
		format(s, taskName, cmp, totalWork, pcnt);
		s.append(BlankConstants.NEW_LINE_CHAR);
		send(s);
	}

	private void format(StringBuilder s, String taskName, int cmp, int totalWork, int pcnt) {
		s.append(taskName);
		s.append(": ");
		while (s.length() < 25) {
			s.append(' ');
		}
		String endStr = String.valueOf(totalWork);
		String curStr = String.valueOf(cmp);
		while (curStr.length() < endStr.length()) {
			curStr = " " + curStr;
		}
		if (pcnt < 100) {
			s.append(' ');
		}
		if (pcnt < 10) {
			s.append(' ');
		}
		s.append(pcnt);
		s.append("% (");
		s.append(curStr);
		s.append("/");
		s.append(endStr);
		s.append(")");
	}

	private void send(StringBuilder s) {
		if(this.deployAbstract != null){
			this.logMessageDto.setLog(s.toString());
			this.deployAbstract.sendLogMessage(this.logMessageDto, this.recvId);
		}else{
			System.err.println(s);
		}
	}

}
