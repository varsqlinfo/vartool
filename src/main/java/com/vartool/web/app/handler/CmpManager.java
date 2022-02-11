package com.vartool.web.app.handler;

public interface CmpManager {
	
	public boolean existsLog(String uid);

	public String getLogContent(String uid);

	public void addLogInfo(String uid, String logText);

	public void removeLogInfo(String uid);

	public void clearLogInfo(String uid) ;
}
