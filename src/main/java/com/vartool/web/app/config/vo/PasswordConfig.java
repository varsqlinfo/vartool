package com.vartool.web.app.config.vo;

import lombok.Getter;

@Getter
public class PasswordConfig {
	private String crpyto;
	private String secretKey;
	private String resetMdoe;
	private int initSize;
	private String type;
}
