package com.vartool.web.constants;

/**
 * web socket constants 
* 
* @fileName	: WebSocketConstants.java
* @author	: ytkim
 */
public interface WebSocketConstants {
	final String APP_DESTINATION_PREFIX = "/app";
	final String USER_DESTINATION_PREFIX = "/user";
	final String ENDPOINT_PREFIX = "/ws";
	
	//final String DESTINATION_PREFIX = 
	enum Type{
		USER_TOPIC("topic")	// 다중 
		,USER_QUEUE_LOG("queue/log"); // 단일 메시지
		//,FILE("file");
		
		String typeCode;
		String clientDestination; 
		String endPoint; 
		String destMatcher;
		
		Type(String typeCode){
			this(typeCode, typeCode);
		}
		
		Type(String typeCode, String endPoint){
			this(typeCode, endPoint, typeCode);
		}
		
		Type(String typeCode, String endPoint, String destMatcher){
			this.typeCode = typeCode;
			
			if(typeCode.startsWith("queue/")) {
				this.clientDestination =  String.format("/%s", typeCode);
			}else {
				this.clientDestination =  String.format("%s/%s", USER_DESTINATION_PREFIX, typeCode);
			}
			
			
			if(endPoint != null) {
				this.endPoint = String.format("%s/%s", ENDPOINT_PREFIX, typeCode);
			}
			
			if(typeCode.equals(destMatcher)) {
				this.destMatcher = String.format("%s/%s%s", USER_DESTINATION_PREFIX, destMatcher, "/**");
			}else {
				this.destMatcher = String.format("%s/%s", USER_DESTINATION_PREFIX, destMatcher);
			}
		}

		public String getTypeCode() {
			return typeCode;
		}
		
		public String getClientDestination() {
			return clientDestination;
		}

		public String getEndPoint() {
			return endPoint;
		}

		public String getDestMatcher() {
			return destMatcher;
		}
	}
}
