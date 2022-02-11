package com.vartool.web.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CmpMonitorDTO {
	
	private String cmpId;
	
	private String cmpType;
	
	private String name; 
	
	private String path; 
	
	private int currentLogSize;
	
	private boolean running;

	public static CmpMonitorDTO toDto(CmpResponseDTO item) {
		CmpMonitorDTO dto = new CmpMonitorDTO();
		
		dto.setCmpId(item.getCmpId());
		dto.setName(item.getName());
		dto.setCmpType(item.getCmpType());
		
		return dto;
	}
	
}