package com.vartool.web.model.entity.cmp;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.vartool.web.constants.ComponentConstants;
import com.vartool.web.model.entity.base.AbstractRegAuditorModel;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@DynamicUpdate
@NoArgsConstructor
@Entity
@Table(name = CmpItemLogEntity._TB_NAME)
@DiscriminatorValue(ComponentConstants.LOG_TYPE_NAME)
public class CmpItemLogEntity extends CmpEntity{
	public final static String _TB_NAME = "VT_CMP_ITEM_LOG";

	@Column(name = "LOG_CHARSET")
	private String logCharset;

	@Column(name = "LOG_PATH")
	private String logPath;
	
	@Builder
	public CmpItemLogEntity(String cmpId, String name, String cmpType, String description, String logCharset, String logPath) {
		super(cmpId, name, cmpType, description);
		this.logCharset = logCharset;
		this.logPath = logPath;
	}

	public final static String LOG_CHARSET = "logCharset";
	public final static String LOG_PATH = "logPath";
}