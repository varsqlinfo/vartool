package com.vartool.web.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CmpGroupCmpMappingResponseDTO {

	private String groupId;
	private String cmpId;
	private String name;
	private String cmpType; 
	private String logPattern;
	private String description;
	
	public CmpGroupCmpMappingResponseDTO(String groupId, String cmpId, String name, String cmpType) {
		this(groupId, cmpId, name, cmpType, null, null);
	}
	
	public CmpGroupCmpMappingResponseDTO(String groupId, String cmpId, String name, String cmpType, String logPattern, String description) {
		this.groupId = groupId;
		this.cmpId = cmpId;
		this.name = name; 
		this.cmpType = cmpType; 
		this.logPattern = logPattern; 
		this.description = description; 
	}
}