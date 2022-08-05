package com.vartool.web.security;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.stereotype.Component;

import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.constants.RequestResultCode;
import com.vartech.common.utils.VartechUtils;
import com.vartool.web.constants.VartoolConstants;
import com.vartool.web.module.SecurityUtils;
import com.vartool.web.module.VartoolUtils;

@Component
public class VartoolAccessDeniedHandler implements AccessDeniedHandler {
	private String accessDeniedUrl;
	private String csrfDeniedUrl;

	private final Logger logger = LoggerFactory.getLogger(VartoolAccessDeniedHandler.class);

	public VartoolAccessDeniedHandler() {
	}

	public VartoolAccessDeniedHandler(String accessDeniedUrl) {
		this(accessDeniedUrl , "");
	}
	public VartoolAccessDeniedHandler(String accessDeniedUrl  , String csrfDeniedUrl) {
		this.accessDeniedUrl = accessDeniedUrl;
		this.csrfDeniedUrl = accessDeniedUrl;
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {

		if(VartoolUtils.isAjaxRequest(request)){

			response.setContentType(VartoolConstants.JSON_CONTENT_TYPE);
			response.setStatus(HttpStatus.OK.value());

			ResponseResult result = new ResponseResult();

			if(SecurityUtils.isAuthenticated()){
				if (accessDeniedException instanceof CsrfException && !response.isCommitted()) {
					result.setResultCode(RequestResultCode.PRECONDITION_FAILED);
				}else{
					result.setResultCode(RequestResultCode.FORBIDDEN);
				}
			}else{
				result.setResultCode(RequestResultCode.LOGIN_INVALID);
			}

			try (Writer writer = response.getWriter()){
				writer.write(VartechUtils.objectToJsonString(result));
			} catch (IOException e) {
				logger.error("VartoolAccessDeniedHandler " ,e);
			}
		}else{
			if (accessDeniedException instanceof CsrfException && !response.isCommitted()) {
	            Cookie cookie = new Cookie("CSRF-TOKEN", "");
	            cookie.setMaxAge(0);
	            cookie.setPath("/");
	            response.addCookie(cookie);
	            response.sendRedirect(request.getContextPath()+csrfDeniedUrl);
	        }else{
	        	response.sendRedirect(request.getContextPath()+accessDeniedUrl);
	        }
		}
	}

	public String getAccessDeniedUrl() {
		return accessDeniedUrl;
	}

	public void setAccessDeniedUrl(String accessDeniedUrl) {
		this.accessDeniedUrl = accessDeniedUrl;
	}
}