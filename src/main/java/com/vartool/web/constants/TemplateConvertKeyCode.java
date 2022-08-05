package com.vartool.web.constants;

import com.vartech.common.constants.CodeEnumValue;

/**
 * template key code
* 
* @fileName	: TemplateConvertKeyCode.java
* @author	: ytkim
 */
public interface TemplateConvertKeyCode {
	enum DEPLOY implements CodeEnumValue{
		SOURCE_PATH("sourcePath")
		,BUILD_PATH("buildPath")
		,DEPENDENCY_PATH("dependencyPath")
		,DEFAULT_DEPENDENCY_PATH("defaultDependencyPath")
		,DEPLOY_PATH("deployPath");
		
		
		private String key; 
		
		DEPLOY(String key) {
			this.key = key;
		}

		public int getCode() {
			return ordinal();
		}
		
		public String getKey() {
			return this.key; 
		}
	}
	
	
}
