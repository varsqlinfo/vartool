package com.vartool.test;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
	
	@Test
	public void contextLoads() {
		//String password = "admin1234!"; 
		String password = "user3!"; 
		
		String encPw = new BCryptPasswordEncoder().encode(password);
		
		System.out.println("111111");
		System.out.println(encPw);
	}

}
