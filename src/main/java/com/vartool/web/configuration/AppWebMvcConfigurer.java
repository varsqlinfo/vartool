package com.vartool.web.configuration;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.vartool.core.config.VartoolConfiguration;
import com.vartool.web.app.common.interceptor.BoardAuthInterceptor;
import com.vartool.web.app.common.interceptor.LanguageInterceptor;
import com.vartool.web.constants.VartoolConstants;
import com.vartool.web.constants.ViewPageConstants;
import com.vartool.web.module.VartoolUtils;

/**
 * web 설정.
* 
* @fileName	: AppWebMvcConfigurer.java
* @author	: ytkim
 */
@Configuration
@ComponentScan(
	basePackages = { "com.vartool.web"},
	includeFilters = {
		@ComponentScan.Filter(type = FilterType.ANNOTATION, value = { Controller.class, Service.class, Repository.class, Component.class }),
		@ComponentScan.Filter(type = FilterType.REGEX, pattern = "(service|controller|DAO|Repository)\\.\\.*")
	}
	,excludeFilters = {
		@ComponentScan.Filter(type = FilterType.REGEX, pattern = "(configuration)\\.*")	
	}
)
@Import(value = {
       AppMainConfigurer.class
})
@EnableAutoConfiguration(exclude = ErrorMvcAutoConfiguration.class)  // "/error" request mapping 를 spring 기본을 사용하지 않기 위해 설정.
public class AppWebMvcConfigurer extends AppWebConfigurer {

    private static final int CACHE_PERIOD = 31556926; // one year

    @PostConstruct
    public void init() {
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix(ViewPageConstants.VIEW_PREFIX);
        resolver.setSuffix(ViewPageConstants.VIEW_SUFFIX);
        resolver.setOrder(2);
        //resolver.setViewClass(JstlView.class);
        registry.viewResolver(resolver);
    }

    /**
     * @method  : reloadableResourceBundleMessageSource
     * @desc : 매시지 처리
     * @author   : ytkim
     * @date   : 2020. 4. 21.
     * @return
     */
    @Bean(name = "messageSource")
    public ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource() {
    	ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:nl/messages", "classpath:nl/label/label");
        messageSource.setDefaultEncoding(VartoolConstants.CHAR_SET);

        if(VartoolUtils.isRuntimelocal()) {
        	messageSource.setCacheSeconds(3);
        }
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }


	@Bean
    public MessageSourceAccessor messageSourceAccessor() {
        return new MessageSourceAccessor(reloadableResourceBundleMessageSource());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Static ressources from both WEB-INF and webjars
        registry
            .addResourceHandler("/webstatic/**")
                .addResourceLocations("/webstatic/","classpath:/META-INF/resources/webstatic/")
                .setCachePeriod(CACHE_PERIOD);

    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        // Serving static files using the Servlet container's default Servlet.
        configurer.enable();
    }

    @Override
    public void addFormatters(FormatterRegistry formatterRegistry) {
        // add your custom formatters
    }

    @Override
	public void addInterceptors(InterceptorRegistry registry) {
    	registry.addInterceptor(boardInterceptor()).addPathPatterns("/board/**");
    	registry.addInterceptor(languageInterceptor()).excludePathPatterns(SecurityConfigurer.WEB_RESOURCES).addPathPatterns("/**");
	}


    /**
     * @method  : languageInterceptor
     * @desc : 다국어 처리.
     * @author   : ytkim
     * @date   : 2020. 4. 21.
     * @return
     */
    @Bean
    public LanguageInterceptor languageInterceptor() {
    	return new LanguageInterceptor();
    }
    
    @Bean
    public BoardAuthInterceptor boardInterceptor() {
    	return new BoardAuthInterceptor();
    }

    @Bean
    public LocaleResolver localeResolver()
    {
        final SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        return localeResolver;
    }

    @Bean
    public MultipartResolver multipartResolver() {
       CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
       multipartResolver.setMaxUploadSize(VartoolConfiguration.getInstance().getFileUploadSize());
       multipartResolver.setMaxUploadSizePerFile(VartoolConfiguration.getInstance().getFileUploadSizePerFile());
       multipartResolver.setDefaultEncoding(VartoolConstants.CHAR_SET);
       multipartResolver.setMaxInMemorySize(VartoolConfiguration.getInstance().getFileUploadMaxInMemorySize());
       return multipartResolver;
    }
}
