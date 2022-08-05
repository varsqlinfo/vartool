package com.vartool.web.app.common.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.apt.Configuration;
import com.vartech.common.app.beans.MailInfo;
import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.constants.RequestResultCode;
import com.vartech.common.utils.DateUtils;
import com.vartech.common.utils.VartechUtils;
import com.vartool.core.config.VartoolConfiguration;
import com.vartool.web.constants.MailType;
import com.vartool.web.constants.ResourceConfigConstants;
import com.vartool.web.model.entity.user.EmailTokenEntity;
import com.vartool.web.model.entity.user.UserEntity;
import com.vartool.web.module.ConvertUtils;
import com.vartool.web.module.VartoolUtils;
import com.vartool.web.repository.EmailTokenEntityRepository;
import com.vartool.web.repository.UserRepository;

@Service
public class UserCommonService {
	private final Logger logger = LoggerFactory.getLogger(UserCommonService.class);
	
	private EmailTokenEntityRepository emailTokenEntityRepository;
	
	private UserRepository userRepository;
	
	private MailService mailService; 
	
	public UserCommonService(EmailTokenEntityRepository emailTokenEntityRepository, UserRepository userRepository, MailService mailService) {
		this.emailTokenEntityRepository = emailTokenEntityRepository; 
		this.userRepository = userRepository; 
		this.mailService = mailService; 
	}
	

	public ResponseResult sendPasswordMail(String uid, String uemail) {
		logger.debug("resetPassword uid :{}, uemail:{} ", uid, uemail);
		
		UserEntity userInfo= userRepository.findByUid(uid);
		
		if(userInfo == null || !userInfo.getUemail().equals(uemail)) {
			return ResponseResult.builder().resultCode( RequestResultCode.NOT_FOUND).build();
		}
		
		EmailTokenEntity tokenInfo = EmailTokenEntity.builder().token(VartechUtils.generateUUID()).viewid(userInfo.getViewid()).tokenType(MailType.RESET_PASSWORD.getType()).build();
		
		String passwordResetUrl = VartoolConfiguration.getInstance().getSiteAddr()+"/resetPassword?token="+tokenInfo.getToken();
		
		logger.debug("password reset url : {}", passwordResetUrl);
		
		emailTokenEntityRepository.save(tokenInfo);
		
		return mailService.sendMail(MailInfo.builder()
			.subject("Vartool password reset")
			.from(VartoolConfiguration.getInstance().getMailConfig().getFromEmail())
			.to(uemail)
			.content(passwordResetUrl)
			.build());
		
	}
	
	/**
	 * token 유효성 체크. 
	 * @param token
	 * @return
	 */
	public boolean isValidToken(String token) {
		
		EmailTokenEntity tokenInfo = emailTokenEntityRepository.findByToken(token);
		
		if(tokenInfo == null) return false; 
		
		int val = DateUtils.dateDiff(ConvertUtils.localDateTimeToDate(tokenInfo.getRegDt()), new Date(), DateUtils.DateCheckType.DAY);
		
		if(val < 1) {
			return true; 
		}
		
		return false;
	}

	
	/**
	 * password 초기화
	 * @param token
	 * @param upw
	 * @param confirmUpw
	 * @return
	 */
	@Transactional(value=ResourceConfigConstants.APP_TRANSMANAGER, rollbackFor=Exception.class)
	public ResponseResult resetPassword(String token, String upw, String confirmUpw) {
		
		if(!upw.equals(confirmUpw)) {
			return VartoolUtils.getResponseResultItemOne("password");
		}
		EmailTokenEntity tokenInfo = emailTokenEntityRepository.findByToken(token);
		
		if(tokenInfo == null) {
			return VartoolUtils.getResponseResultItemOne("token");
		}
		
		int val = DateUtils.dateDiff(ConvertUtils.localDateTimeToDate(tokenInfo.getRegDt()), new Date(), DateUtils.DateCheckType.DAY);
		
		if(val >= 1) {
			return VartoolUtils.getResponseResultItemOne("token");
		}
		
		UserEntity entity= userRepository.findByViewid(tokenInfo.getViewid());
		
		entity.setUpw(upw);
		entity = userRepository.save(entity);
		
		emailTokenEntityRepository.deleteTokenInfo(tokenInfo.getToken(), tokenInfo.getTokenType());
		
		return VartoolUtils.getResponseResultItemOne("success");
	}

	
	
	
}
