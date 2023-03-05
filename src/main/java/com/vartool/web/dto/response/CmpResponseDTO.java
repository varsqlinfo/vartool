package com.vartool.web.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CmpResponseDTO {

	private String cmpId;
	private String name;
	private String cmpType;
	private String description;
	private String cmpCredential;
	private String logPattern;
	
	private LocalDateTime regDt;
	
	public CmpResponseDTO() {}
	
	public CmpResponseDTO(String cmpId, String name, String cmpType, String logPattern, String description, String cmpCredential) {
		this.cmpId = cmpId;
		this.name = name; 
		this.cmpType = cmpType;
		this.description = description;
		this.cmpCredential = cmpCredential;
	}
}