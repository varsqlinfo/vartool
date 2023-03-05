package com.vartool.web.dto.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
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
		}else if (size >= this.maxSize) {
			for (int i = this.maxSize; i < size; i++) {
				logTextArr.poll();
			}
		}

		logTextArr.add(logStr);
	}
	
	// 큐 rear에 데이터 등록
	public void add(Deque<String> logList) {
		int addLogSize = logList.size(); 
		
		if (addLogSize >= this.maxSize) {
			logTextArr.clear();
			logTextArr.addAll(new ArrayList<String>(logList).subList(addLogSize- this.maxSize, addLogSize));
			return ;
		}
		
		int size = logTextArr.size() + addLogSize;
		
		if (size > this.maxSize) {
			for (int i = this.maxSize; i < size; i++) {
				logTextArr.poll();
			}
		}
		
		logTextArr.addAll(logList);
	}
	
	public Collection<String> allLog() {
	    return new ArrayList<String>(logTextArr); 
	}

	public int getLogSize() {
		return this.logTextArr.size();
	}

	public void clear() {
		logTextArr.clear();
	}
}
