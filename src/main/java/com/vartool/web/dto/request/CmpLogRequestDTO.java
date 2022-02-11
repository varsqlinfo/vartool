package com.vartool.web.dto.request;

import com.vartool.web.model.entity.cmp.CmpItemLogEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CmpLogRequestDTO extends CmpRequestDTO {
	private String charset;
	private String logPath;
	
	public CmpItemLogEntity toEntity() {
		return CmpItemLogEntity.builder()
			.name(getName())
			.cmpId(getCmpId())
			.description(getDescription())
			.logCharset(charset)
			.logPath(logPath)
			.build();
	}
	
	
}