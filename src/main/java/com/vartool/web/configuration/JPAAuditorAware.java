package com.vartool.web.configuration;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vartool.web.security.User;

/**
 * jpa audit
* 
* @fileName	: JPAAuditorAware.java
* @author	: ytkim
 */
public class JPAAuditorAware implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (null == authentication || !authentication.isAuthenticated()) {
			return Optional.empty();
		}
		
		if(authentication.getPrincipal() instanceof User) {
			return Optional.of(((User) authentication.getPrincipal()).getViewid());
		}else {
			return Optional.of(authentication.getPrincipal().toString());
		}
		
	}
}
