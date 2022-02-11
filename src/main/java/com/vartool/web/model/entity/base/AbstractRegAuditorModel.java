package com.vartool.web.model.entity.base;
import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
@EntityListeners(value = { AuditingEntityListener.class })
public abstract class AbstractRegAuditorModel implements Serializable{

	private static final long serialVersionUID = 7893362241769774670L;

	@CreatedBy
    @Column(name="REG_ID", nullable = false, updatable = false)
    private String regId;
	
	@JsonFormat(shape = Shape.STRING ,pattern=VartoolConstants.DATE_TIME_FORMAT)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @CreatedDate
    @Column(name="REG_DT", nullable = false, updatable = false)
    private LocalDateTime regDt;
	
	
	public final static String REG_ID = "regId";
	public final static String REG_DT = "regDt";
}