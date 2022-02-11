package com.vartool.web.constants;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface ComponentConstants{
	public String COMMAND_TYPE_NAME  = "COMMAND"; 
	public String LOG_TYPE_NAME  = "LOG"; 
	public String DEPLOY_TYPE_NAME  = "DEPLOY"; 
	
	enum TYPE {
		COMMAND(COMMAND_TYPE_NAME)
		,LOG(LOG_TYPE_NAME)
		,DEPLOY(DEPLOY_TYPE_NAME);
		
		@JsonProperty
		private String typeName;
		
		@JsonProperty
		private String code; 

		TYPE(String name) {
			this.typeName = name;
			this.code = this.name();
		}

		public String getTypeName() {
			return typeName;
		}

		public String getCode() {
			return code;
		}
	}
	
	public static TYPE getComponentType(String type) {
		for(TYPE cmpType : TYPE.values()) {
			if(cmpType.getTypeName().equals(type)) {
				return cmpType;
			}
		}
		return null; 
	}
}
