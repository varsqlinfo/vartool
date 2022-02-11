package com.vartool.web.dto.request;

import com.vartool.web.model.entity.cmp.CmpItemCommandEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CmpCommandRequestDTO extends CmpRequestDTO {
	
	private String charset;
	private String startCmd;
	private String stopCmd;
	
	public CmpItemCommandEntity toEntity() {
		return CmpItemCommandEntity.builder()
			.name(getName())
			.cmpId(getCmpId())
			.description(getDescription())
			.stopCmd(stopCmd)
			.startCmd(startCmd)
			.cmdCharset(charset)
			.build();
	}
}