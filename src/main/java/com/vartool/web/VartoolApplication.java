package com.vartool.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.vartool.core.config.Constants;

@SpringBootApplication
@ServletComponentScan
public class VartoolApplication extends SpringBootServletInitializer {
	static {
		String catalinaHome = System.getProperty("catalina.home", "");
		if ("".equals(catalinaHome)) {
			System.setProperty(Constants.RUNTIME_KEY, "local");
			System.setProperty(Constants.ROOT_PATH_KEY, "C:/zzz/vtool/");
			System.setProperty("spring.devtools.restart.enabled", "true");
			System.setProperty("spring.devtools.livereload.enable", "true");
		} 
	}

	public void onStartup(ServletContext servletContext) throws ServletException { 
		super.onStartup(servletContext);
	}

	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(new Class[] { VartoolApplication.class });
	}

	public static void main(String[] args) {
		SpringApplication.run(VartoolApplication.class, args);
	}
}
