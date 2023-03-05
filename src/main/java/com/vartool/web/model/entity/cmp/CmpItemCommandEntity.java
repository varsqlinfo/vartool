package com.vartool.web.model.entity.cmp;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Lob;
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
@Table(name = CmpItemCommandEntity._TB_NAME)
@DiscriminatorValue(ComponentConstants.COMMAND_TYPE_NAME)
public class CmpItemCommandEntity extends CmpEntity{
	private static final long serialVersionUID = 1L;

	public final static String _TB_NAME = "VT_CMP_ITEM_COMMAND";
	
	@Lob
	@Column(name = "START_CMD")
	private String startCmd;

	@Lob
	@Column(name = "STOP_CMD")
	private String stopCmd;
	
	@Column(name = "CMD_CHARSET")
	private String cmdCharset;
	
	@Builder
	public CmpItemCommandEntity (String cmpId, String name, String cmpType, String logPattern, String description, String startCmd ,String stopCmd, String cmdCharset, String cmpCredential) {
		super(cmpId, name, cmpType, logPattern, description, cmpCredential);
		this.startCmd = startCmd;
		this.stopCmd = stopCmd;
		this.cmdCharset = cmdCharset;
	}

	public final static String START_CMD = "startCmd";
	public final static String STOP_CMD = "stopCmd";
	public final static String CMD_CHARSET = "cmdCharset";
}