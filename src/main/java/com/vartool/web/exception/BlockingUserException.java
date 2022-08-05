package com.vartool.web.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 사용자 block 
* 
* @fileName	: BlockingUserException.java
* @author	: ytkim
 */
public class BlockingUserException extends AuthenticationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BlockingUserException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}


}
