package com.vartool.web.module;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.vartool.web.constants.ResourceConfigConstants;

/**
 * 패스워드 utils 
* 
* @fileName	: PasswordUtils.java
* @author	: ytkim
 */
public final class PasswordUtils {
	
	
	public static String encode (String pw) {
		return ((PasswordEncoder)VartoolBeanUtils.getStringBean(ResourceConfigConstants.APP_PASSWORD_ENCODER)).encode(pw);
	}
	
	public static String decode (String pw) {
		return pw; 
	}
}
