package com.vartool.web.dto.request;

import com.vartool.web.model.entity.cmp.CmpItemCommandEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * 
* 
* @fileName	: CmpCommandRequestDTO.java
* @author	: ytkim
 */
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
			.cmpCredential(getCmpCredential())
			.logPattern(getLogPattern())
			.description(getDescription())
			.stopCmd(stopCmd)
			.startCmd(startCmd)
			.cmdCharset(charset)
			.build();
	}
}