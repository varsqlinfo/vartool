package com.vartool.web.dto.request;

import com.vartool.web.model.entity.cmp.CmpItemLogEntity;
import com.vartool.web.model.entity.cmp.CmpItemLogExtensionsEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CmpLogRequestDTO extends CmpRequestDTO {
	private String logType;
	private String charset;
	private String logPath;
	
	private String remoteHost; 
	private int remotePort; 
	private String command;
	
	public CmpItemLogEntity toEntity() {
		
		return CmpItemLogEntity.builder()
			.name(getName())
			.cmpId(getCmpId())
			.cmpCredential(getCmpCredential())
			.description(getDescription())
			.logPattern(getLogPattern())
			.logType(logType)
			.logCharset(charset)
			.logPath(logPath)
			.cmpItemLogExtensionsEntity(CmpItemLogExtensionsEntity.builder()
				.cmpId(getCmpId())
				.remoteHost(remoteHost)
				.remotePort(remotePort)
				.command(command)
				.build())
			.build();
	}
	
	
}