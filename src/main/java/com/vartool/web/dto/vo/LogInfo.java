package com.vartool.web.dto.vo;

import java.util.concurrent.ConcurrentLinkedDeque;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class LogInfo {

	private int maxSize;
	
	@JsonIgnore
	private ConcurrentLinkedDeque<String> logTextArr;

	public LogInfo(int maxSize) {
		this.maxSize = maxSize;
		this.logTextArr = new ConcurrentLinkedDeque<String>();
	}

	// 큐 rear에 데이터 등록
	public void add(String logStr) {
		
		int size = logTextArr.size();
		
		if (size == this.maxSize) {
			logTextArr.poll();
		}else if (size > this.maxSize) {
			for (int i = this.maxSize; i < size; i++) {
				logTextArr.poll();
			}
		}

		logTextArr.add(logStr);
	}
	
	public String allLog() {
		
		StringBuffer sb = new StringBuffer();
		
		for (String str: logTextArr) {
			sb.append(str);
		}
		
		return sb.toString();
		
	}

	public int getLogSize() {
		return this.logTextArr.size();
	}

	public void clear() {
		logTextArr.clear();
	}
}
