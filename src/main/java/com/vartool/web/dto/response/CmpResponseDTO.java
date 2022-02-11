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
	
	private LocalDateTime regDt;
	
	public CmpResponseDTO() {}
	
	public CmpResponseDTO(String cmpId, String name, String cmpType) {
		this.cmpId = cmpId;
		this.name = name; 
		this.cmpType = cmpType;
	}
}