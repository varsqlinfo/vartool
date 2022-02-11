package com.vartool.web.security;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vartool.web.constants.LocaleConstants;
import com.vartool.web.constants.ResourceConfigConstants;
import com.vartool.web.model.entity.user.UserEntity;
import com.vartool.web.security.auth.Authority;
import com.vartool.web.security.auth.AuthorityType;
import com.vartool.web.security.repository.UserRepository;

/**
 * 로그인 사용자 체크.
 * @FileName : AuthDAO.java
 * @Author   : ytkim
 * @Program desc : 인증 dao
 * @Hisotry :
 */
@Service(value = "authDao")
public final class AuthDAO {

	private final Logger logger = LoggerFactory.getLogger(AuthDAO.class);

	@Autowired
	private UserRepository userRepository;


	@Autowired
	@Qualifier(ResourceConfigConstants.APP_PASSWORD_ENCODER)
	private PasswordEncoder passwordEncoder;

	/**
	 *
	 * @Method Name  : loadUserByUsername
	 * @Method 설명 : 사용자 로그인 정보 추출.
	 * @작성일   : 2015. 6. 22.
	 * @작성자   : ytkim
	 * @변경이력  :
	 * @param username
	 * @return
	 * @throws UsernameNotFoundException
	 */
	public User loadUserByUsername(final String username) throws UsernameNotFoundException {
		return loadUserByUsername(username, null, true);
	}

	public User loadUserByUsername(String username, String password, boolean remembermeFlag) {
		try {
			UserEntity userModel= userRepository.findByUid(username);

			if(userModel==null){
				return null;
				//throw new UsernameNotFoundException("Wrong username or password ");
			}

			if(remembermeFlag==false) {
				if(!passwordEncoder.matches(password, userModel.getUpw())){
					return null;
					//throw new UsernameNotFoundException("Wrong username or password ");
				}
			}

			User user = new User();
			user.setLoginRememberMe(remembermeFlag);
			user.setViewid(userModel.getViewid());
			user.setUsername(userModel.getUid());
			user.setPassword("");
			user.setFullname(userModel.getUname());

			if(userModel.isBlockYn()){ //차단된 사용자 체크.
				user.setBlockYn(true);
				return user;
			}

			user.setUserLocale(LocaleConstants.parseLocaleString(userModel.getLang()));
			user.setEmail(userModel.getUemail());
			user.setAcceptYn(userModel.isAcceptYn());

			String userRole = userModel.getUserRole();


			List<Authority> authList = new ArrayList<Authority>();
			Authority r = new Authority();

			AuthorityType authType = AuthorityType.getType(userRole);
			r = new Authority();
			r.setName(authType.name());
			r.setPriority(authType.getPriority());
			authList.add(r);
			
			user.setTopAuthority(authType);
			user.setAuthorities(authList);

			return user;
		}catch(Exception e){
			logger.error(this.getClass().getName() , e);
			throw new UsernameNotFoundException(new StringBuilder().append("Wrong username or password :").append(username).append(" ").append(e.getMessage()).toString());
		}
	}

	/**
	 *
	 * @Method Name  : passwordCheck
	 * @Method 설명 : 사용자 비밀번호 체크
	 * @작성일   : 2019. 9. 20.
	 * @작성자   : ytkim
	 * @변경이력  :
	 * @throws Exception
	 */
	public boolean passwordCheck(String username, String password) {
		UserEntity userModel= userRepository.findByUid(username);

		if(userModel==null){
			return false;
		}

		if(!passwordEncoder.matches(password, userModel.getUpw())){
			return false;
		}

		return true;
	}


}
