package com.vartool.web.constants;

public enum UploadFileType {
	APPFILE("appFile", "appFile")
	,BOARD("board", "board")
	,BOARD_CONTENT_IMAGE("boardContentImage", "boardContentImage")
	,TEMP("temp", "temp")
	,OTHER("other", "other");
	
	private String div;
	private String savePathRoot;
	private boolean orginFileName;
	private SavePathType savePathType;
	
	UploadFileType(String div, String savePathRoot){
		this(div, savePathRoot, false);
	}
	
	UploadFileType(String div, String savePathRoot, boolean orginFileName){
		this(div, savePathRoot, orginFileName, SavePathType.DATE_YY_MM);
	}
	
	UploadFileType(String div, String savePathRoot, boolean orginFileName, SavePathType dateYyMm){
		this.div = div; 
		this.savePathRoot = savePathRoot; 
		this.orginFileName = orginFileName; 
		this.savePathType = dateYyMm; 
	}

	public String getDiv() {
		return div;
	}

	public SavePathType getSavePathType() {
		return savePathType;
	}
	
	public String getSavePathRoot() {
		return savePathRoot;
	}
	
	public boolean isOrginFileName() {
		return orginFileName;
	}

	public static UploadFileType getDivType(String div) {
		for (UploadFileType uft : UploadFileType.values()) {
			if(uft.getDiv().equals(div)) {
				return uft; 
			}
		}
		return OTHER;
	}

}
