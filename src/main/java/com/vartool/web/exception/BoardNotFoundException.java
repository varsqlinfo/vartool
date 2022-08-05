package com.vartool.web.exception;

/**
 * board not found exception
* 
* @fileName	: BoardNotFoundException.java
* @author	: ytkim
 */
public class BoardNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int errorCode;
	private String errorMessage;
	private String messageCode;
	
	/**
	 * 
	 */
	public BoardNotFoundException() {
		super();
	}
	/**
	 * @param s java.lang.String
	 */
	public BoardNotFoundException(String s) {
		super(s);
	}
	/**
	 * @param s java.lang.String
	 */
	public BoardNotFoundException(String s , Exception exeception) {
		super(s,exeception);
	}
	
	
	public BoardNotFoundException(int errorCode,Exception exeception) {
		this(errorCode,null, exeception);
	}
	
	public BoardNotFoundException(int errorCode, String messageCode ,Exception exeception) {
		this(errorCode, messageCode , null, exeception);
	}
	public BoardNotFoundException(int errorCode,String messageCode,	String errorMessage, Exception exeception) {
		super(errorMessage, exeception);
		this.errorCode=errorCode ;
		this.messageCode=messageCode;
		this.errorMessage = errorMessage;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
