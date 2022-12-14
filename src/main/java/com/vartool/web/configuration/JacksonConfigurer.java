package com.vartool.web.configuration;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.vartool.web.constants.VartoolConstants;

/**
 * jackson 설정. 
* 
* @fileName	: JacksonConfigurer.java
* @author	: ytkim
 */
public class JacksonConfigurer implements Jackson2ObjectMapperBuilderCustomizer {
 
	@Override
	public void customize(Jackson2ObjectMapperBuilder builder) {
		builder.timeZone(TimeZone.getDefault()); // 올바른 타임존을 설정해야 offset/zoned datetime이 올바로 설정됨.
        builder.locale(Locale.getDefault());      
        builder.simpleDateFormat(VartoolConstants.DATE_FORMAT);
        builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(VartoolConstants.DATE_FORMAT)));
        builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(VartoolConstants.TIMESTAMP_FORMAT)));
		
	}
 
}