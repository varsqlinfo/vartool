package com.vartool.web.model.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * yn Converter
* 
* @fileName	: BooleanToYnConverter.java
* @author	: ytkim
 */
@Converter
public class BooleanToYnConverter implements AttributeConverter<Boolean, String> {

	@Override
	public String convertToDatabaseColumn(Boolean attribute) {
		return (attribute == null || attribute) ? "Y" : "N";
	}
	
	@Override
	public Boolean convertToEntityAttribute(String s) {
		return "Y".equals(s);
	}
}