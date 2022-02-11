package com.vartool.web.security;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.vartool.web.security.auth.Authority;
import com.vartool.web.security.auth.AuthorityType;
 
public class User implements UserDetails {
 
    private static final long serialVersionUID = -4450269958885980297L;
    
    final public static String ANONYMOUS_USERNAME = "anonymous";
	final public static String ANONYMOUS_ROLE = "ROLE_ANONYMOUS";
	
	private String viewid;
    private String username;
	private String password;
	
	private String fullname;
	
	private String email;
	
	private AuthorityType topAuthority;
	
	private boolean loginRememberMe;
	
	private boolean acceptYn;
	
	private boolean blockYn;
	
	private String userIp;
	
	private Locale userLocale;
	
	private List<Authority> authorities;
	private boolean accountNonExpired = true;
	private boolean accountNonLocked = true;
	private boolean credentialsNonExpired = true;
	private boolean enabled = true;
     
	public User(){
    }
     
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }
    
    public void setName(String username) {
		this.username = username;
	}
    
    public String getName() {
		return this.username;
	}
    
    public void setUsername(String username) {
		this.username = username;
	}
    
    public String getUsername() {
        return username;
    }

	public void setPassword(String password) {
		this.password = password;
	}
  
    public String getPassword() {
        return password;
    }
    
    public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
  
    public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setAuthorities(List<Authority> authorities) {
		this.authorities = authorities;
	}
	
	public String getViewid() {
		return viewid;
	}

	public void setViewid(String viewid) {
		this.viewid = viewid;
	}

	public Locale getUserLocale() {
		return userLocale;
	}

	public void setUserLocale(Locale userLocale) {
		this.userLocale = userLocale;
	}

	public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}


	public boolean isLoginRememberMe() {
		return loginRememberMe;
	}

	public void setLoginRememberMe(boolean loginRememberMe) {
		this.loginRememberMe = loginRememberMe;
	}


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	public boolean isAcceptYn() {
		return acceptYn;
	}

	public void setAcceptYn(boolean acceptYn) {
		this.acceptYn = acceptYn;
	}


	public boolean isBlockYn() {
		return blockYn;
	}

	public void setBlockYn(boolean blockYn) {
		this.blockYn = blockYn;
	}


	public AuthorityType getTopAuthority() {
		return topAuthority;
	}

	public void setTopAuthority(AuthorityType topAuthority) {
		this.topAuthority = topAuthority;
	}


	public static class AnonymousUser {
		public AnonymousUser() {
		}
		
		public User build() {
			User user = new User();
			user.setUsername(ANONYMOUS_USERNAME);
			return user;
		}
	}
 }