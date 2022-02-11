package com.vartool.web.module;

/**
 *
 * url replace util 
 * 
* 2021. 10. 5. 		ytkim			최초작성

*-----------------------------------------------------------------------------
 */
public final class UrlReplaceUtils {
	enum UrlSpecialChar{
		AMPERSAND("&","_a-")
		,EQUALS("=","_e-")
		,QUESTION_MARK("?","_q-");
		
		private String code;
		private String replaceStr;

		UrlSpecialChar(String code, String replaceStr) {
			this.code = code;
			this.replaceStr = replaceStr;
		}

		public String getCode() {
			return code;
		}

		public String getReplaceStr() {
			return replaceStr;
		}
	}
	
	
	public static String encodeURLReplace(String str) {
		
		for (UrlSpecialChar item: UrlSpecialChar.values()) {
			str = str.replaceAll("\\"+item.getCode(), item.getReplaceStr());
		} 
		
		return str;
	}
	
	public static String decodeURLReplace(String str) {
		
		for (UrlSpecialChar item: UrlSpecialChar.values()) {
			str = str.replaceAll(item.getReplaceStr(), item.getCode());
		} 
		
		return str;
	}
}
