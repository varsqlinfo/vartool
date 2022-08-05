package com.vartool.web.app.handler.deploy.git;

/**
 * 파일 변경 정보
* 
* @fileName	: ChangeInfo.java
* @author	: ytkim
 */
public class ChangeInfo {
    
	private String changeInfo; 
	private String oldPath; 
	private String newPath;
	
	public ChangeInfo(String changeInfo, String oldPath, String newPath) {
		this.changeInfo =changeInfo; 
		this.oldPath =oldPath; 
		this.newPath =newPath; 
	}
   
	public String getChangeInfo() {
		return changeInfo;
	}
	public void setChangeInfo(String changeInfo) {
		this.changeInfo = changeInfo;
	}
	public String getOldPath() {
		return oldPath;
	}
	public void setOldPath(String oldPath) {
		this.oldPath = oldPath;
	}
	public String getNewPath() {
		return newPath;
	}
	public void setNewPath(String newPath) {
		this.newPath = newPath;
	} 
   
    
}