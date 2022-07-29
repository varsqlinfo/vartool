package com.vartool.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


/**
 * 
 * @FileName : UserService.java
 * @Author   : ytkim
 * @Program desc : 
 * @Hisotry :
 */
public class UserService implements UserDetailsService {
	
	@Autowired
	@Qualifier("authDao")
	private AuthDAO authDao;

	@Override
	public User loadUserByUsername(final String username) throws UsernameNotFoundException {
		return authDao.loadUserByUsername(username);
	}

	public User loadUserByUsername(String username, String password) {
		return authDao.loadUserByUsername(username, password, false);
	}
	
	public boolean passwordCheck(String username, String password) {
		return authDao.passwordCheck(username, password);
	}
}
