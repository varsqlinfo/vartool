package com.vartool.web.dto.response;

import com.vartool.web.model.entity.cmp.CmpItemLogEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CmpLogResponseDTO extends CmpResponseDTO {
	private String charset;
	private String logPath;
	private String logType;
	
	private String remoteHost;
	private int remotePort;
	private String command;
	
	public static CmpLogResponseDTO toDto(CmpItemLogEntity entity) {
		CmpLogResponseDTO dto= new CmpLogResponseDTO();
		
		
		dto.setCmpId(entity.getCmpId());
		dto.setName(entity.getName());
		dto.setCmpCredential(entity.getCmpCredential());
		dto.setCmpType(entity.getCmpType());
		dto.setLogType(entity.getLogType());
		dto.setDescription(entity.getDescription());
		dto.setRegDt(entity.getRegDt());
		
		dto.setCharset(entity.getLogCharset());
		dto.setLogPath(entity.getLogPath());
		
		return dto;
	}
}