package com.vartool.web.model.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vartech.common.crypto.EncryptDecryptException;
import com.vartool.core.crypto.PasswordCryptionFactory;

/**
 * password converter
* 
* @fileName	: AppPasswordEncodeConverter.java
* @author	: ytkim
 */
@Converter
public class AppPasswordEncodeConverter implements AttributeConverter<String, String> {
	private final Logger logger = LoggerFactory.getLogger(AppPasswordEncodeConverter.class);

	@Override
	public String convertToDatabaseColumn(String attribute) {
		if(attribute==null || "".equals(attribute)) return null;

		try {
			return PasswordCryptionFactory.getInstance().encrypt(attribute);
		} catch (EncryptDecryptException e) {
			logger.error("AppPasswordEncodeConverter : {} ", e.getMessage() , e);
		}
		return null;
	}

	@Override
	public String convertToEntityAttribute(String s) {
		if(s==null || "".equals(s)) return null;
		return s;
	}

}