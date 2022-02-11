package com.vartool.web.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

/**
 *
 * @FileName  : VarsqlAuthenticationLogoutHandler.java
 * @프로그램 설명 : log out handler
 * @Date      : 2019. 9. 21.
 * @작성자      : ytkim
 * @변경이력 :
 */
@Component
public class VartoolAuthenticationLogoutHandler implements LogoutHandler {
	private final Logger logger = LoggerFactory.getLogger(VartoolAuthenticationLogoutSuccessHandler.class);

    public void logout(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) {

    	if (authentication != null && authentication.getDetails() != null) {
    		logger.error("VarsqlAuthenticationLogoutSuccessHandler  onLogoutSuccess :{}" , authentication);
		}
    }
}