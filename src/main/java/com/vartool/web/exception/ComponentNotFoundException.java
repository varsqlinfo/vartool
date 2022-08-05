package com.vartool.web.exception;

/**
 * component not found exception
* 
* @fileName	: ComponentNotFoundException.java
* @author	: ytkim
 */
public class ComponentNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  
  public ComponentNotFoundException() {}
  
  public ComponentNotFoundException(Throwable cause) {
    super(cause);
  }
  
  public ComponentNotFoundException(String s) {
    super(s);
  }
  
  public ComponentNotFoundException(String s, Exception exeception) {
    super(s, exeception);
  }
}
