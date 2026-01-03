package com.vartool.web.app.common.controller.handler;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.vartool.core.config.VartoolConfiguration;

/**
 * common controller advice
* 
* @fileName	: CommonControllerAdvice.java
* @author	: ytkim
 */
@ControllerAdvice
public class CommonControllerAdvice {
  @ModelAttribute
  public void handleRequest(HttpServletRequest request, Model model) {
    String contextPath = request.getContextPath();
    
    model.addAttribute("pageContextPath", contextPath);
    model.addAttribute("loginUrl", contextPath+"/login_check");
    model.addAttribute("logoutUrl", contextPath+"/logout");
    model.addAttribute("fileUploadSize", VartoolConfiguration.getInstance().getFileUploadSize());
    model.addAttribute("fileUploadSizePerFile", VartoolConfiguration.getInstance().getFileUploadSizePerFile());
    
  }
}