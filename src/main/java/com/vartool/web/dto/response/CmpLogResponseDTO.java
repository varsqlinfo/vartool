package com.vartool.web.dto.response;

import com.vartool.web.model.entity.cmp.CmpEntity;
import com.vartool.web.model.entity.cmp.CmpItemLogEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CmpLogResponseDTO extends CmpResponseDTO {
	private String charset;
	private String logPath;
	
	public static CmpLogResponseDTO toDto(CmpItemLogEntity entity) {
		CmpLogResponseDTO dto= new CmpLogResponseDTO();
		
		dto.setName(entity.getName());
		dto.setCmpId(entity.getCmpId());
		dto.setCmpType(entity.getCmpType());
		dto.setDescription(entity.getDescription());
		dto.setRegDt(entity.getRegDt());
		
		dto.setCharset(entity.getLogCharset());
		dto.setLogPath(entity.getLogPath());
		
		return dto;
	}
}