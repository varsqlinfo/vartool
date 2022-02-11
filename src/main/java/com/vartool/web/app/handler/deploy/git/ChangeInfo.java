package com.vartool.web.app.handler.deploy.git;

/**
 * 
 * @FileName  : ChangeInfo.java
 * @프로그램 설명 : 파일 변경 정보
 * @Date      : 2019. 1. 8. 
 * @작성자      : ytkim
 * @변경이력 :
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