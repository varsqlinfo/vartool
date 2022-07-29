package com.vartool.web.security;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.vartool.web.app.login.LoginController;
import com.vartool.web.exception.BlockingUserException;

@Component
public class VartoolAuthenticationProvider implements AuthenticationProvider { 
      
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    
	private UserService userService;
     
    public VartoolAuthenticationProvider(UserService userService) {
    	this.userService= userService; 
    }

	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String password = (String) authentication.getCredentials();
		
		User user = userService.loadUserByUsername(username , password);
		
		if (user == null) {
			throw new BadCredentialsException("Username not match.");	
		}
		
		if(user.isBlockYn()){
			throw new BlockingUserException("block user");
		}

		return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
	}

	
	public boolean supports(Class<?> arg0) {
		return true;
	}
}