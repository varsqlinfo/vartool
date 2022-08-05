package com.vartool.web.app.common.service;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import com.vartech.common.app.beans.MailInfo;
import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.utils.StringUtils;
import com.vartool.web.constants.ResourceConfigConstants;
import com.vartool.web.constants.VartoolConstants;

/**
 * mail service
* 
* @fileName	: MailService.java
* @author	: ytkim
 */
@Service
public class MailService {
	private final Logger logger = LoggerFactory.getLogger(MailService.class);
	
	private JavaMailSender mailSender;
	
	public MailService(@Qualifier(ResourceConfigConstants.MAIL_SERVICE) JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	public ResponseResult sendMail(MailInfo mailInfo) {
		
		final MimeMessagePreparator preparator = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
            	
                final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StringUtils.isBlank(mailInfo.getCharset())? VartoolConstants.CHAR_SET : mailInfo.getCharset());
                
                helper.setFrom(mailInfo.getFrom()); 
                helper.setTo(mailInfo.getTo());
                helper.setSubject(mailInfo.getSubject()); // mail title
                helper.setText(mailInfo.getContent(), true); // mail content
            }
        };

        try {
        	mailSender.send(preparator);
        }catch(Exception e) {
        	logger.error("mailServer :{}", e.getMessage(), e);
        	
        	//return ResponseResult.builder().message(e.getMessage()).build();
        }
		
		return ResponseResult.builder().item(1).build();
	}
	
}
