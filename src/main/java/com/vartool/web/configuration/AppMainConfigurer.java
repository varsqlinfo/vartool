package com.vartool.web.configuration;

import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;

import com.vartool.web.constants.VartoolConstants;

/**
 * main config
* 
* @fileName	: AppMainConfigurer.java
* @author	: ytkim
 */
@Import(value = {
	JPAConfigurer.class
	,AppTilesConfigurer.class
    ,SecurityConfigurer.class
    ,MailConfigurer.class
    //,ServiceConfigurer.class
})
public class AppMainConfigurer {

	private final Logger LOG = LoggerFactory.getLogger(AppMainConfigurer.class);

    @Autowired
    private Environment env;

    /**
     * Application custom initialization code.
     * <p/>
     * Spring profiles can be configured with a system property
     * -Dspring.profiles.active=javaee
     * <p/>
     */
    @PostConstruct
    public void initApp() {
        LOG.debug("Looking for Spring profiles...");
        if (env.getActiveProfiles().length == 0) {
            LOG.info("No Spring profile configured, running with default configuration.");
        } else {
            for (String profile : env.getActiveProfiles()) {
                LOG.info("Detected Spring profile: {}", profile);
            }
        }
    }

    @Bean()
    public StringHttpMessageConverter converter() {
		StringHttpMessageConverter converter = new StringHttpMessageConverter();

        converter.setSupportedMediaTypes(new ArrayList<MediaType>(){
			private static final long serialVersionUID = 1735295099855086282L;

		{
        	add(new MediaType("text", "plain", Charset.forName(VartoolConstants.CHAR_SET)));
        }});;
        return converter;
    }
}
