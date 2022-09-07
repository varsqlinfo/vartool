package com.vartool.web.model.mapper;

import java.sql.Timestamp;
import java.util.Date;

import org.mapstruct.Mapper;

import com.vartech.common.utils.DateUtils;
import com.vartool.web.constants.VartoolConstants;

@Mapper(componentModel = "spring")
public class DateMapper {

	public String asString(Date date) {
		return date != null ? DateUtils.format(VartoolConstants.DATE_FORMAT, date) : null;
	}

	public Date asDate(String date) {
		return date != null ? DateUtils.stringToDate(date): null;
	}

	public String asTimestampString(Timestamp date) {
		return date != null ? DateUtils.format(VartoolConstants.TIME_FORMAT, date) : null;
	}

	public Timestamp asTimestamp(String date) {
		return date != null ? DateUtils.stringToTimestamp(date)  : null;
	}
}