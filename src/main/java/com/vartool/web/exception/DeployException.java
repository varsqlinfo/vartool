package com.vartool.web.exception;

public class DeployException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  
  public DeployException() {}
  
  public DeployException(Throwable cause) {
    super(cause);
  }
  
  public DeployException(String s) {
    super(s);
  }
  
  public DeployException(String s, Exception exeception) {
    super(s, exeception);
  }
}
