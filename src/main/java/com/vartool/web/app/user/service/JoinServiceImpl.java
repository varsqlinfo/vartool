package com.vartool.web.app.user.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.crypto.EncryptDecryptException;
import com.vartech.common.utils.VartechUtils;
import com.vartool.web.dto.request.UserReqeustDTO;
import com.vartool.web.model.entity.user.UserEntity;
import com.vartool.web.module.VartoolUtils;
import com.vartool.web.repository.UserRepository;
import com.vartool.web.security.auth.AuthorityType;

/**
 *
 * @FileName  : AdminServiceImpl.java
 * @Date      : 2014. 8. 18.
 * @작성자      : ytkim
 * @변경이력 :
 * @프로그램 설명 :
 */
@Service
public class JoinServiceImpl{
	private final Logger logger = LoggerFactory.getLogger(JoinServiceImpl.class);

	@Autowired
	private UserRepository userRepository; 

	/**
	 * @method  : saveUser
	 * @desc : 사용자 정보 등록.
	 * @author   : ytkim
	 * @date   : 2020. 4. 27.
	 * @param joinForm
	 * @return
	 * @throws EncryptDecryptException
	 */
	public boolean saveUser(UserReqeustDTO joinForm) throws EncryptDecryptException {
		logger.debug("saveUser {} " , VartechUtils.reflectionToString(joinForm));
		UserEntity entity = joinForm.toEntity();

		entity.setUserRole(AuthorityType.GUEST.name());
		entity.setAcceptYn(false);

		userRepository.save(entity);

		return true;
	}

	/**
	 * @method  : idCheck
	 * @desc : id check
	 * @author   : ytkim
	 * @date   : 2020. 4. 27.
	 * @param uid
	 * @return
	 */
	public ResponseResult idCheck(String uid) {
		return VartoolUtils.getResponseResultItemOne(userRepository.countByUid(uid));
	}

	/**
	 * @method  : emailCheck
	 * @desc : email check
	 * @author   : ytkim
	 * @date   : 2020. 4. 27.
	 * @param uemail
	 * @return
	 */
	public ResponseResult emailCheck(String uemail) {
		return VartoolUtils.getResponseResultItemOne(userRepository.countByUemail(uemail));
	}
}