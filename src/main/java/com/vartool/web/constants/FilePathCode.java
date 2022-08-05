package com.vartool.web.constants;

/**
 * file path 
* 
* @fileName	: FilePathCode.java
* @author	: ytkim
 */
public enum FilePathCode {
	EXPORT_PATH("exportFile"),
	IMPORT("fileImport"), 
	UPLOAD("appFile"), 
	BOARD("brdFile", false), 
	OTHER("appOther");

	private String div;
	private boolean dbSave;

	FilePathCode(String div) {
		this(div,true);
	}

	FilePathCode(String div, boolean dbSaveFlag) {
		this.div = div;
		this.dbSave = dbSaveFlag;
	}

	public static FilePathCode getFileType(String div) {
		if (div != null)
			for (FilePathCode type : values()) {
				if (div.equalsIgnoreCase(type.name()))
					return type;
			}
		return OTHER;
	}

	public String getDiv() {
		return this.div;
	}
	
	public boolean isDbSave() {
		return this.dbSave;
	}
}
