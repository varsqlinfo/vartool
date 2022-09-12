package com.vartool.web.model.entity.cmp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.vartool.web.constants.ComponentConstants;

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
	
	private static final long serialVersionUID = 1L;

	public final static String _TB_NAME = "VT_CMP_ITEM_LOG";

	@Column(name = "LOG_CHARSET")
	private String logCharset;

	@Column(name = "LOG_TYPE")
	private String logType;
	
	@Column(name = "LOG_PATH")
	private String logPath;
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "CMP_ID")
    private CmpItemLogExtensionsEntity cmpItemLogExtensionsEntity;
	
	@Builder
	public CmpItemLogEntity(String cmpId, String name, String cmpType, String description, String logCharset, String logType, String logPath, String cmpCredential, CmpItemLogExtensionsEntity cmpItemLogExtensionsEntity) {
		super(cmpId, name, cmpType, description, cmpCredential);
		this.logCharset = logCharset;
		this.logType = logType;
		this.logPath = logPath;
		this.cmpItemLogExtensionsEntity = cmpItemLogExtensionsEntity;
	}

	public final static String LOG_CHARSET = "logCharset";
	public final static String LOG_PATH = "logPath";
}