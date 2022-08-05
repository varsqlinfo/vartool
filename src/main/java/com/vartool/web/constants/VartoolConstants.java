package com.vartool.web.constants;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.vartool.core.config.VartoolConfiguration;

/**
 * vartool constants
* 
* @fileName	: VartoolConstants.java
* @author	: ytkim
 */
public interface VartoolConstants {
	
	// 날짜 포켓.
	final String YEAR_FORMAT = "yyyy";
	
	final String DATE_FORMAT = "yyyy-MM-dd";

	final String TIME_FORMAT = "HH:mm:ss";
	
	final String TIME_MILLISECOND_FORMAT = "HH:mm:ss.SSS";

	final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
	final String TIMESTAMP_MILLISECOND_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	final DateTimeFormatter yearFormatter = DateTimeFormat.forPattern(YEAR_FORMAT);
	final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern(DATE_FORMAT);
	final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern(TIME_FORMAT);
	final DateTimeFormatter timeMilliFormatter = DateTimeFormat.forPattern(TIME_MILLISECOND_FORMAT);
	final DateTimeFormatter timestampFormatter = DateTimeFormat.forPattern(TIMESTAMP_FORMAT);
	final DateTimeFormatter timestampMilliFormatter = DateTimeFormat.forPattern(TIMESTAMP_MILLISECOND_FORMAT);
	
    
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
