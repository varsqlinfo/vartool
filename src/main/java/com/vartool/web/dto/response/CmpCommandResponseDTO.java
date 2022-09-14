package com.vartool.web.dto.response;

import com.vartool.web.model.entity.cmp.CmpItemCommandEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CmpCommandResponseDTO extends CmpResponseDTO {
	
	private String charset;
	private String startCmd;
	private String stopCmd;
	
	public static CmpCommandResponseDTO toDto(CmpItemCommandEntity entity) {
		CmpCommandResponseDTO dto= new CmpCommandResponseDTO();
		
		dto.setName(entity.getName());
		dto.setCmpId(entity.getCmpId());
		dto.setCmpType(entity.getCmpType());
		dto.setCmpCredential(entity.getCmpCredential());
		dto.setDescription(entity.getDescription());
		dto.setRegDt(entity.getRegDt());
		dto.setCharset(entity.getCmdCharset());
		dto.setStartCmd(entity.getStartCmd());
		dto.setStopCmd(entity.getStopCmd());
		
		return dto;
	}
	
	
}