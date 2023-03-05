package com.vartool.web.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CmpRequestDTO {

	private String cmpId;
	private String name;
	private String cmpType;
	private String description;
	private String cmpCredential;
	private String logPattern;
}