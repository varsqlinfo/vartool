package com.vartool.web.configuration;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.vartech.common.utils.VartechReflectionUtils;
import com.vartool.core.config.VartoolConfiguration;
import com.vartool.core.config.vo.MailConfig;
import com.vartool.web.constants.ResourceConfigConstants;

/**
 * main 설정
* 
* @fileName	: MailConfigurer.java
* @author	: ytkim
 */
public class MailConfigurer {
	
	private final Logger logger = LoggerFactory.getLogger(MailConfigurer.class);
    
    @Bean(ResourceConfigConstants.MAIL_SERVICE)
    public JavaMailSender getMailSender() {
    	
    	MailConfig mailConfig = VartoolConfiguration.getInstance().getMailConfig();
    	
    	JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        if(!mailConfig.isEnableMail()) return mailSender; 
        
        mailSender.setHost(mailConfig.getHost());
        mailSender.setPort(mailConfig.getPort());
        
        if(mailConfig.isSmtpAuth()) {
        	 mailSender.setUsername(mailConfig.getUsername());
             mailSender.setPassword(mailConfig.getPassword());
        }
        
        Properties javaMailProperties = new Properties();
        
        logger.info("mailConfig : {} ", VartechReflectionUtils.reflectionToString(mailConfig));
        
        javaMailProperties.put("mail.smtp.starttls.enable", mailConfig.isStarttlsEnable());
        javaMailProperties.put("mail.smtp.auth", mailConfig.isSmtpAuth());
        javaMailProperties.put("mail.transport.protocol", mailConfig.getTransportProtocol());
        javaMailProperties.put("mail.debug", mailConfig.isDebug());
 
        mailSender.setJavaMailProperties(javaMailProperties);
        
        return mailSender;
    }
}


