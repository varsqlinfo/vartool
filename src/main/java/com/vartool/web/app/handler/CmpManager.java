package com.vartool.web.app.handler;

import java.util.Collection;
import java.util.Deque;

/**
 * component manager interface
* 
* @fileName	: CmpManager.java
* @author	: ytkim
 */
public interface CmpManager {
	
	public boolean existsLog(String uid);

	public Collection<String> getLogContent(String uid);

	public void addLogInfo(String uid, Deque<String> logText);

	public void removeLogInfo(String uid);

	public void clearLogInfo(String uid) ;
}
