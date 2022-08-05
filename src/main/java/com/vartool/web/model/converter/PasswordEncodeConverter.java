package com.vartool.web.model.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.vartool.web.module.PasswordUtils;

/**
 * password converter
* 
* @fileName	: PasswordEncodeConverter.java
* @author	: ytkim
 */
@Converter
public class PasswordEncodeConverter implements AttributeConverter<String, String> {
	
	@Override
	public String convertToDatabaseColumn(String attribute) {
		return PasswordUtils.encode(attribute);
	}
	
	@Override
	public String convertToEntityAttribute(String s) {
		return s;
	}
	
}