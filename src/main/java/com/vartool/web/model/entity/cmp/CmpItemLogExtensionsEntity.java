package com.vartool.web.model.entity.cmp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.vartech.common.utils.StringUtils;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@DynamicUpdate
@NoArgsConstructor
@Entity
@Table(name = CmpItemLogExtensionsEntity._TB_NAME)
public class CmpItemLogExtensionsEntity{

	public final static String _TB_NAME="VT_CMP_ITEM_LOG_EXTENSIONS";

	@Id
	@Column(unique = true, nullable = false)
	private String cmpId; 

	@Column(name ="REMOTE_HOST")
	private String remoteHost; 

	@Column(name ="REMOTE_PORT")
	private int remotePort; 

	@Column(name ="COMMAND")
	private String command;
	
	@MapsId
	@OneToOne(optional = true)
	@JoinColumn(name = "CMP_ID")
	private CmpItemLogEntity cmpItemLogEntity;

	@Builder
	public CmpItemLogExtensionsEntity (String cmpId, String remoteHost, int remotePort, String command) {
		this.cmpId = cmpId;
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
		this.command = command;
	}

	public final static String CMP_ID="cmpId";
	public final static String REMOTE_HOST="remoteHost";
	public final static String REMOTE_PORT="remotePort";
	public final static String COMMAND="command";
	
	@PrePersist
	public void setCmpId () {
		if(StringUtils.isBlank(this.cmpId)) {
			this.cmpId = cmpItemLogEntity.getCmpId();
		}
	}
}