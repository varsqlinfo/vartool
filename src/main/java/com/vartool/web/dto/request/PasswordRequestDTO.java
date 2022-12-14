package com.vartool.web.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.vartool.web.dto.valid.ValidPassword;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordRequestDTO {
	
	private String viewid;
	
	private String currPw;
	
	@NotBlank
	@Size(max=500)
	@ValidPassword
	private String upw;
	
	@NotBlank
	@Size(max=500)
	private String confirmUpw;

}