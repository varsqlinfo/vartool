package com.vartool.web.constants;

/**
 * 환경설정 상수
* 
* @fileName	: PreferencesConstants.java
* @author	: ytkim
 */
public interface PreferencesConstants {
	public enum PREFKEY {
		MAIN_LAYOUT("main.layout.setting");

		String prefKey;

		PREFKEY(String key) {
			this.prefKey = key;
		}

		public String key() {
			return this.prefKey;
		}

		public String toString() {
			return this.prefKey + "";
		}
	}
	
	public static String getPrefKey(String viewKey) {
		for (PREFKEY key: PREFKEY.values()) {
			if(key.name().equalsIgnoreCase(viewKey)) {
				return key.key();
			}
		}
		return null; 
	};
}
