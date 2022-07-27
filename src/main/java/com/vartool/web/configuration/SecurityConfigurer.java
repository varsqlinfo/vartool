package com.vartool.web.configuration;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

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
	private final String CSRF_TOKEN_NAME = "vartool-csrf";
	
	public static final String WEB_RESOURCES = "/webstatic/**";

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
	
	private OrRequestMatcher staticRequestMatcher = new OrRequestMatcher(
		new AntPathRequestMatcher(WEB_RESOURCES)
		, new AntPathRequestMatcher("/error/**")
		, new AntPathRequestMatcher("/**/favicon.ico")
		, new AntPathRequestMatcher("/favicon.ico")
	);
	
	// ajax header matcher
	private RequestMatcher ajaxRequestMatcher = new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest");


	@Override
    public void configure(WebSecurity web) throws Exception {

		// 404 error 처리 하기위해서 추가.
		StrictHttpFirewall firewall = new StrictHttpFirewall();
		firewall.setAllowUrlEncodedDoubleSlash(true);

        web.ignoring()
            .requestMatchers(staticRequestMatcher)
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
			.ignoringAntMatchers("/login/**","/join/**","/logout")
			.ignoringRequestMatchers(staticRequestMatcher)
			.requireCsrfProtectionMatcher(ajaxRequestMatcher)
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
     		.antMatchers("/login","/join/**","/lostPassword","/resetPassword").anonymous()
     		.antMatchers("/login_check","/error/**","/common/**","/index.jsp").permitAll()
     		.antMatchers("/**").authenticated()
     		.anyRequest().authenticated()
     	.and()
     		.exceptionHandling()
     		.defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.NOT_FOUND), staticRequestMatcher)
     		.defaultAuthenticationEntryPointFor(restAuthenticationEntryPoint(), ajaxRequestMatcher)
     		.accessDeniedHandler(accessDeniedHandler())
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
	
	// ajax call entry point
	private AuthenticationEntryPoint restAuthenticationEntryPoint() {
		return  new AuthenticationEntryPoint () {

			@Override
			public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
				request.getRequestDispatcher("/invalidLogin").forward(request, response);
			}
		};
	}
	
	private CsrfTokenRepository getCookieCsrfTokenRepository() {
		CookieCsrfTokenRepository csrfCookie = new CookieCsrfTokenRepository();
		csrfCookie.setCookieHttpOnly(true);
		csrfCookie.setCookieName(CSRF_TOKEN_NAME);
		csrfCookie.setHeaderName(CSRF_TOKEN_NAME);
		csrfCookie.setParameterName(CSRF_TOKEN_NAME);
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
