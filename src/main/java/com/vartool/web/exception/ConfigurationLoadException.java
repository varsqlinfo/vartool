package com.vartool.web.exception;

/**
 *  Configuration Load Exception
* 
* @fileName	: ConfigurationLoadException.java
* @author	: ytkim
 */
public class ConfigurationLoadException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  
  public ConfigurationLoadException() {}
  
  public ConfigurationLoadException(Throwable cause) {
    super(cause);
  }
  
  public ConfigurationLoadException(String s) {
    super(s);
  }
  
  public ConfigurationLoadException(String s, Exception exeception) {
    super(s, exeception);
  }
}
