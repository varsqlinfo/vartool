package com.vartool.core.config.vo;

import lombok.Getter;

/**
 * application password crypto config
 * 
* @fileName	: PasswordConfig.java
* @author	: ytkim
 */
@Getter
public class PasswordConfig {
	private String crpyto;
	private String secretKey;
	private String customClass;
}
