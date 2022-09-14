package com.vartool.web.model.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.vartool.core.crypto.PasswordCryptionFactory;

/**
 * password converter
* 
* @fileName	: AppPasswordEncodeConverter.java
* @author	: ytkim
 */
@Converter
public class AppPasswordEncodeConverter implements AttributeConverter<String, String> {
	
	@Override
	public String convertToDatabaseColumn(String attribute) {
		if(attribute==null || "".equals(attribute)) return null;

		return PasswordCryptionFactory.getInstance().encrypt(attribute);
	}

	@Override
	public String convertToEntityAttribute(String s) {
		if(s==null || "".equals(s)) return null;
		return s;
	}

}