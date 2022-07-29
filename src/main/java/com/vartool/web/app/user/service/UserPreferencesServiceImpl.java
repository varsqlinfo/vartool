package com.vartool.web.app.user.service;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.crypto.EncryptDecryptException;
import com.vartool.web.app.common.service.AbstractService;
import com.vartool.web.constants.AppCode;
import com.vartool.web.constants.LocaleConstants;
import com.vartool.web.constants.ResourceConfigConstants;
import com.vartool.web.dto.request.PasswordRequestDTO;
import com.vartool.web.dto.request.UserModReqeustDTO;
import com.vartool.web.exception.VartoolAppException;
import com.vartool.web.model.entity.user.UserEntity;
import com.vartool.web.module.SecurityUtil;
import com.vartool.web.repository.UserMgmtRepository;

@Service
public class UserPreferencesServiceImpl extends AbstractService{
	private final Logger logger = LoggerFactory.getLogger(UserPreferencesServiceImpl.class);


	@Autowired
	private UserMgmtRepository userMgmtRepository;

	@Autowired
	@Qualifier(ResourceConfigConstants.APP_PASSWORD_ENCODER)
	private PasswordEncoder passwordEncoder;

	/**
	 *
	 * @Method Name  : selectUserDetail
	 * @Method 설명 : 사용자 정보 상세.
	 * @작성자   : ytkim
	 * @작성일   : 2017. 11. 29.
	 * @변경이력  :
	 * @param loginId
	 * @return
	 */
	public UserEntity findUserInfo(String viewid) {
		return userMgmtRepository.findByViewid(viewid);
	}

	/**
	 *
	 * @Method Name  : updateUserInfo
	 * @Method 설명 : 사용자 정보 업데이트
	 * @작성자   : ytkim
	 * @작성일   : 2017. 11. 29.
	 * @변경이력  :
	 * @param userForm
	 * @param req
	 * @param res
	 * @return
	 */
	public boolean updateUserInfo(UserModReqeustDTO userForm, HttpServletRequest req, HttpServletResponse res) {

		logger.debug("updateUserInfo : {}" , userForm);

		UserEntity userInfo = userMgmtRepository.findByViewid(SecurityUtil.userViewId());

		if(userInfo==null) throw new VartoolAppException("user infomation not found : " + SecurityUtil.userViewId());

		userInfo.setLang(userForm.getLang());
		userInfo.setUname(userForm.getUname());
		userInfo.setOrgNm(userForm.getOrgNm());
		userInfo.setDeptNm(userForm.getDeptNm());
		userInfo.setDescription(userForm.getDescription());

		userMgmtRepository.save(userInfo);

		// 언어 변경시 처리.
		Locale userLocale= LocaleConstants.parseLocaleString(userForm.getLang());

		if(userLocale != null  && !userLocale.equals(SecurityUtil.loginInfo().getUserLocale())) {
			LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(req);
			if (localeResolver == null) {
				throw new IllegalStateException("No LocaleResolver found.");
			}

			if(localeResolver.resolveLocale(req) != userLocale) {
				localeResolver.setLocale(req, res, userLocale);
			}

			SecurityUtil.loginInfo().setUserLocale(userLocale);
		}

		return true;
	}

	/**
	 *
	 * @Method Name  : updatePasswordInfo
	 * @Method 설명 : 비밀번호 변경.
	 * @작성자   : ytkim
	 * @작성일   : 2017. 11. 29.
	 * @변경이력  :
	 * @param passwordForm
	 * @param resultObject
	 * @return
	 * @throws EncryptDecryptException
	 */
	public ResponseResult updatePasswordInfo(PasswordRequestDTO passwordForm) throws EncryptDecryptException {

		UserEntity userInfo = userMgmtRepository.findByViewid(SecurityUtil.userViewId());

		ResponseResult resultObject = new ResponseResult();

		if(passwordEncoder.matches(passwordForm.getCurrPw(), userInfo.getUpw())){
			userInfo.setUpw(passwordForm.getUpw());
			userMgmtRepository.save(userInfo);
			resultObject.setItemOne(1);
		}else{
			resultObject.setResultCode(AppCode.ErrorCode.COMM_PASSWORD_NOT_VALID);
		}

		return resultObject;
	}

	

}