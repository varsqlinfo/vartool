package com.vartool.web.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.vartool.web.constants.ResourceConfigConstants;
import com.vartool.web.security.RestAuthenticationEntryPoint;
import com.vartool.web.security.UserService;
import com.vartool.web.security.VartoolAccessDeniedHandler;
import com.vartool.web.security.VartoolAuthenticationFailHandler;
import com.vartool.web.security.VartoolAuthenticationLogoutHandler;
import com.vartool.web.security.VartoolAuthenticationLogoutSuccessHandler;
import com.vartool.web.security.VartoolAuthenticationProvider;
import com.vartool.web.security.VartoolAuthenticationSuccessHandler;
import com.vartool.web.security.auth.AuthorityType;

/**
 * -----------------------------------------------------------------------------
* @fileName		: SecurityConfig.java
* @desc		: security configuration
* @author	: ytkim
*-----------------------------------------------------------------------------
  DATE			AUTHOR			DESCRIPTION
*-----------------------------------------------------------------------------
*2020. 4. 21. 			ytkim			최초작성

*-----------------------------------------------------------------------------
 */
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {
	private final String CSRF_NAME = "vartool-csrf";

	@Autowired
	private VartoolAuthenticationProvider vartoolAuthenticationProvider;
	
	@Autowired
	private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
	
	@Autowired
	private VartoolAuthenticationSuccessHandler varsqlAuthenticationSuccessHandler;

	@Autowired
	private VartoolAuthenticationFailHandler varsqlAuthenticationFailHandler;

	@Autowired
	private VartoolAuthenticationLogoutHandler varsqlAuthenticationLogoutHandler;

	@Autowired
	private VartoolAuthenticationLogoutSuccessHandler varsqlAuthenticationLogoutSuccessHandler;

	@Autowired
	private UserService userService;

	@Override
    public void configure(WebSecurity web) throws Exception {

		// 404 error 처리 하기위해서 추가.
		StrictHttpFirewall firewall = new StrictHttpFirewall();
		firewall.setAllowUrlEncodedDoubleSlash(true);

        web.ignoring()
            .antMatchers("/webstatic/**","/error/**","/favicon.ico")
         .and().httpFirewall(firewall);
    }

	@Override
    protected void configure(HttpSecurity http) throws Exception {
		configureHttpSecurity(http);
    }

	private void configureHttpSecurity(HttpSecurity http) throws Exception {

		http.headers()
			.frameOptions().sameOrigin().httpStrictTransportSecurity()
			.disable()
		.and()
			.csrf()
			.csrfTokenRepository(getCookieCsrfTokenRepository())
			.ignoringAntMatchers("/login/**","/logout","/webstatic/**","/error/**","/favicon.ico")
			.requireCsrfProtectionMatcher(new CsrfRequestMatcher())
		.and()
			//.addFilterBefore(new CsrfCookieGeneratorFilter(), CsrfFilter.class)
			.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint)
		.and() //session
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
			.sessionAuthenticationErrorUrl("/login")  // remmember me error 처리 페이지. 추가. 
			//.maximumSessions(1)	// 중복 로그인 카운트
			.sessionFixation().changeSessionId()	// session 공격시 session id 변경.
		.and() // login
			.formLogin()
	        .loginPage("/login")
	        .loginProcessingUrl("/login_check")
	        .usernameParameter("vtool_login_id")
	        .passwordParameter("vtool_login_password")
	        .successHandler(varsqlAuthenticationSuccessHandler)
	        .failureHandler(varsqlAuthenticationFailHandler)
	        .permitAll()
	    .and() // auth
		    .authorizeRequests()
     		.antMatchers("/admin/**").hasAuthority(AuthorityType.ADMIN.name())
     		.antMatchers("/mgmt/**").hasAnyAuthority(AuthorityType.ADMIN.name(),AuthorityType.MANAGER.name())
     		.antMatchers("/user/**","/main/**").hasAnyAuthority(AuthorityType.ADMIN.name(),AuthorityType.MANAGER.name(),AuthorityType.USER.name())
     		.antMatchers("/guest/**").hasAuthority(AuthorityType.GUEST.name())
     		.antMatchers("/login","/join/**").anonymous()
     		.antMatchers("/login_check","/error/**","/common/**","/index.jsp").permitAll()
     		.antMatchers("/**").authenticated()
     		.anyRequest().authenticated()
     	.and()
     		.exceptionHandling().accessDeniedHandler(accessDeniedHandler())
     	.and() //log out
	     	.logout()
	        .logoutUrl("/logout")
	        .logoutSuccessUrl("/login")
	        .addLogoutHandler(varsqlAuthenticationLogoutHandler)
	        .logoutSuccessHandler(varsqlAuthenticationLogoutSuccessHandler)
	        .invalidateHttpSession(true)
	        .deleteCookies("JSESSIONID").permitAll()
	        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))

		.and();
	}
	
	private CsrfTokenRepository getCookieCsrfTokenRepository() {
		CookieCsrfTokenRepository csrfCookie = new CookieCsrfTokenRepository();
		csrfCookie.setCookieHttpOnly(true);
		csrfCookie.setCookieName(CSRF_NAME);
		csrfCookie.setHeaderName(CSRF_NAME);
		csrfCookie.setParameterName(CSRF_NAME);
		return csrfCookie;
	}

	@Bean("varsqlRequestCache")
	public RequestCache requestCache() {
	   return new HttpSessionRequestCache();
	}

	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
	    return new HttpSessionEventPublisher();
	}

	@Bean(ResourceConfigConstants.APP_PASSWORD_ENCODER)
    public PasswordEncoder varsqlPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected UserDetailsService userDetailsService() {
    	if(userService==null) {
    		userService = new UserService();
    	}
    	return userService;
    }

    @Bean
    public VartoolAccessDeniedHandler accessDeniedHandler() {
    	return new VartoolAccessDeniedHandler("/error/error403");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	 auth.authenticationProvider(vartoolAuthenticationProvider);
    }

    
}
