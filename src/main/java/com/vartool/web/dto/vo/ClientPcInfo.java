package com.vartool.web.dto.vo;

public class ClientPcInfo {
  private String userAgent;
  
  private String osType;
  
  private String browser;
  
  private String deviceType;
  
  private String ip;
  
  public String getUserAgent() {
    return this.userAgent;
  }
  
  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }
  
  public String getOsType() {
    return this.osType;
  }
  
  public void setOsType(String osType) {
    this.osType = osType;
  }
  
  public String getBrowser() {
    return this.browser;
  }
  
  public void setBrowser(String browser) {
    this.browser = browser;
  }
  
  public String getDeviceType() {
    return this.deviceType;
  }
  
  public void setDeviceType(String deviceType) {
    this.deviceType = deviceType;
  }
  
  public String getIp() {
    return this.ip;
  }
  
  public void setIp(String ip) {
    this.ip = ip;
  }
}
