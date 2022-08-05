package com.vartool.web.constants;

import com.vartech.common.constants.CodeEnumValue;

/**
 * application code
* 
* @fileName	: AppCode.java
* @author	: ytkim
 */
public interface AppCode {
	
	final public String BOARD_CODE ="boardCode";
	final public String DOC_ID ="docId";
	
	enum SERVER_CODE implements CodeEnumValue{
		RUNNING(999)
		,COMPLETE(200);
		
		private int code; 
		
		SERVER_CODE(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}
	}
	
	enum LOG_STATE implements CodeEnumValue{
		STOP(100)
		,START(101);
		
		private int code; 
		
		LOG_STATE(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}
	}
	
	enum SCM_TYPE implements CodeEnumValue{
		GIT(1)
		,SVN(2);
		
		private int code; 
		
		SCM_TYPE(int code) {
			this.code = code;
		}
		
		public int getCode() {
			return code;
		}
		
		public String getViewName() {
			return this.name();
		}
		
		public boolean isEquals(String code) {
			return name().equalsIgnoreCase(code);
		}
	}
	
	enum ErrorCode implements CodeEnumValue {
		SUCCESS(200)
		, ERROR(1000)
		, INVALID_DATABASE(2000)
		, COMM_FILE_EMPTY(50000)
		, COMM_PASSWORD_NOT_VALID(50001)
		, COMM_FILE_UPLOAD_ERROR(50002)
		, COMM_FILE_DOWNLOAD_ERROR(50003)
		, COMM_RUNTIME_ERROR(50005);

		private int code = -1;

		private String message;
		
		ErrorCode(int pcode) {
			this(pcode, null);
		}

		ErrorCode(int pcode, String message) {
			this.code = pcode;
			this.message = message;
		}

		public String toString() {
			return name() + " (" + this.code + ")";
		}

		public int getCode() {
			return this.code;
		}

		public String getDesc() {
			return this.message;
		}
	}
}
