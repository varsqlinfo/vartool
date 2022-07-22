package com.vartool.web.constants;

import com.vartool.web.app.config.VartoolConfiguration;

public interface VartoolConstants {
	
	// 날짜 포켓.
	final String DATE_FORMAT = "yyyy-MM-dd";
    
	final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
	final String CHAR_SET = VartoolConfiguration.getInstance().getDefaultCharset();
	
	final String FILE_ID_DELIMITER = ",";
	
	final String JSON_CONTENT_TYPE = "application/json;charset=" + CHAR_SET;

	final String UPLOAD_PATH = VartoolConfiguration.getInstance().getConfigInfo().getUploadPath();
	
	
	final String TABLE_SEQUENCE_NAME = "VT_SEQ";
	
	// password 잊어 버렸을때 변경 방법. 
	enum PASSWORD_RESET_MODE{
		EMAIL, MANAGER;

		public static PASSWORD_RESET_MODE  getMode(String mode) {
			
			for(PASSWORD_RESET_MODE resetMode : PASSWORD_RESET_MODE.values()) {
				if(resetMode.name().equalsIgnoreCase(mode)) {
					return resetMode;
				}
			}
			
			return PASSWORD_RESET_MODE.MANAGER;
		}
	}
}
