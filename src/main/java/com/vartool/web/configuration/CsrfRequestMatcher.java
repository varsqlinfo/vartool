package com.vartool.web.configuration;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * -----------------------------------------------------------------------------
* @fileName		: CsrfRequestMatcher.java
* @desc		: csrf request matcher
* @author	: ytkim
*-----------------------------------------------------------------------------
  DATE			AUTHOR			DESCRIPTION
*-----------------------------------------------------------------------------
*2020. 4. 21. 			ytkim			최초작성

*-----------------------------------------------------------------------------
 */
public class CsrfRequestMatcher implements RequestMatcher {

    @Override
    public boolean matches(HttpServletRequest request) {

    	String xRequestedWith = request.getHeader("X-Requested-With");

		if (xRequestedWith != null && "XMLHttpRequest".equals(xRequestedWith)) {
			return true;
		}else{
			return false;
		}
    }
}