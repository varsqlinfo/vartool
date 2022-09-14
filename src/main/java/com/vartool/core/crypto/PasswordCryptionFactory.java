package com.vartool.core.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vartech.common.crypto.EncryptDecryptException;
import com.vartech.common.crypto.impl.AESEncryptDecrypt;
import com.vartech.common.crypto.impl.AbstractCrypto;
import com.vartech.common.crypto.impl.VartechSeed;
import com.vartech.common.utils.StringUtils;
import com.vartech.common.utils.VartechReflectionUtils;
import com.vartool.core.config.VartoolConfiguration;
import com.vartool.web.constants.AppCode;
import com.vartool.web.exception.VartoolAppException;

/**
 * Password factory
* 
* @fileName	: PasswordCryptionFactory.java
* @author	: ytkim
 */
public class PasswordCryptionFactory {
	private final Logger logger = LoggerFactory.getLogger(PasswordCryptionFactory.class);

	private AbstractCrypto abstractCrypto;

	private static class ConfigurationHolder{
        private static final PasswordCryptionFactory instance = new PasswordCryptionFactory();
    }

	public static PasswordCryptionFactory getInstance() {
		return ConfigurationHolder.instance;
    }

	private PasswordCryptionFactory(){

		String secretKey = VartoolConfiguration.getInstance().getPwSecurityKey();
		String cryptoType = VartoolConfiguration.getInstance().getPWCryptoType();
		String customClass = VartoolConfiguration.getInstance().getPWCustomClass();
		try {
			logger.debug("db password crypto type  : {}" , cryptoType);
			if("aes".equals(cryptoType.toLowerCase())) {
				abstractCrypto = new AESEncryptDecrypt(secretKey);
			}else if("seed".equals(cryptoType.toLowerCase())) {
				abstractCrypto = new VartechSeed(secretKey);
			}else{
				logger.debug("db password crypto custom class  : {}" , customClass);
				if("".equals(customClass)) {
					throw new EncryptDecryptException("custom crypto class not found : ["+customClass+"]");
				}else {
					abstractCrypto = (AbstractCrypto)VartechReflectionUtils.getConstructorIfAvailable(VartechReflectionUtils.forName(customClass), String.class).newInstance(secretKey);
				}
			}
		}catch(EncryptDecryptException e){
			logger.error("EncryptionFactory init error :  ",e);
		}catch (Exception e) {
			logger.error("db password crypto type  : {} :  ",cryptoType);
			logger.error("db password crypto custom class  : {}" , customClass);
			logger.error("EncryptionFactory init error :  ",e);
		}
	}

	public String encrypt(String enc){
		try {
			return StringUtils.isBlank(enc) ? enc : abstractCrypto.encrypt(enc);
		}catch(Exception e) {
			throw new VartoolAppException(AppCode.ErrorCode.PASSWORD_ENCRYPT.getCode() ,e);
		}
	}

	public String decrypt(String enc){
		try {
			return StringUtils.isBlank(enc) ? enc : abstractCrypto.decrypt(enc);
		}catch(Exception e) {
			throw new VartoolAppException(AppCode.ErrorCode.PASSWORD_DECRYPT.getCode() ,e);
		}
		
		
	}
}
