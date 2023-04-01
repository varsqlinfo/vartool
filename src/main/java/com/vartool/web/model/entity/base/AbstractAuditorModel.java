package com.vartool.web.model.entity.base;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.vartool.web.constants.VartoolConstants;

import lombok.Getter;
import lombok.Setter;

/**
 * -----------------------------------------------------------------------------
* @fileName		: BaseAuditorModel.java
* @desc		: model audit model
* @author	: ytkim
*-----------------------------------------------------------------------------
  DATE			AUTHOR			DESCRIPTION
*-----------------------------------------------------------------------------
*2020. 4. 20. 			ytkim			최초작성

*-----------------------------------------------------------------------------
 */
@Setter
@Getter
@MappedSuperclass
public abstract class AbstractAuditorModel extends AbstractRegAuditorModel{
	
	private static final long serialVersionUID = 7893362241769774670L;
	
	@JsonIgnore
    @LastModifiedBy
    @Column(name="UPD_ID")
    private String updId;
	
	@JsonFormat(shape = Shape.STRING, pattern=VartoolConstants.TIMESTAMP_FORMAT)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @LastModifiedDate
    @Column(name="UPD_DT")
    private LocalDateTime updDt;
	
	
	public final static String UPD_ID = "updId";
	public final static String UPD_DT = "updDt";

}