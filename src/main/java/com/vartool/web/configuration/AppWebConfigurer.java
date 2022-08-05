package com.vartool.web.configuration;

import javax.servlet.ServletContextListener;

import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.vartool.core.config.VartoolConfiguration;
import com.vartool.web.constants.VartoolConstants;


/**
 * app web Configurer
* 
* @fileName	: AppWebConfigurer.java
* @author	: ytkim
 */
public class AppWebConfigurer implements WebMvcConfigurer {
	@Bean
	public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerFactoryCustomizer() {
		return factory -> {
			factory.setContextPath(VartoolConfiguration.getInstance().getContextPath());
		};
	}

	@Bean
	public ServletListenerRegistrationBean<ServletContextListener> listenerRegistrationBean() {
		ServletListenerRegistrationBean<ServletContextListener> bean = new ServletListenerRegistrationBean<>();
		bean.setListener(new ContextLoaderListener());
		return bean;
	}

	@Bean
	public ErrorPageRegistrar errorPageRegistrar(){
	    return new AppErrorPageRegistrar();
	}

	@Bean
	public FilterRegistrationBean filterRegistrationBean() {
	    FilterRegistrationBean<CharacterEncodingFilter> registrationBean = new FilterRegistrationBean<>();
	    CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
	    characterEncodingFilter.setForceEncoding(true);
	    characterEncodingFilter.setEncoding(VartoolConstants.CHAR_SET);
	    registrationBean.setFilter(characterEncodingFilter);
	    registrationBean.setOrder(Integer.MIN_VALUE);
	    registrationBean.addUrlPatterns("/*");
	    return registrationBean;
	}
}
