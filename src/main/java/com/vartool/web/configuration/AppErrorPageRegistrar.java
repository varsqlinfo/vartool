package com.vartool.web.configuration;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.http.HttpStatus;

/**
 * error page 설정.
* 
* @fileName	: AppErrorPageRegistrar.java
* @author	: ytkim
 */
public class AppErrorPageRegistrar implements ErrorPageRegistrar {

	@Override
	public void registerErrorPages(ErrorPageRegistry registry) {
		registry.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/error/error404"));
		registry.addErrorPages(new ErrorPage(HttpStatus.FORBIDDEN, "/error/error403"));
		registry.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/error500"));
		registry.addErrorPages(new ErrorPage(Throwable.class, "/error/error500"));
		registry.addErrorPages(new ErrorPage(NullPointerException.class, "/error/error500"));
	}
}