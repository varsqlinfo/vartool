package com.vartool.web.model.entity.cmp;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vartool.web.constants.ComponentConstants;
import com.vartool.web.model.converter.AppPasswordEncodeConverter;
import com.vartool.web.model.converter.BooleanToYnConverter;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@DynamicUpdate
@NoArgsConstructor
@Entity
@Table(name = CmpItemDeployEntity._TB_NAME)
@DiscriminatorValue(ComponentConstants.DEPLOY_TYPE_NAME)
public class CmpItemDeployEntity extends CmpEntity{
	public final static String _TB_NAME = "VT_CMP_ITEM_DEPLOY";

	@Column(name = "SCM_TYPE")
	private String scmType;

	@Column(name = "SCM_URL")
	private String scmUrl;

	@Column(name = "SCM_ID")
	private String scmId;

	@JsonIgnore
	@Convert(converter = AppPasswordEncodeConverter.class)
	@Column(name = "SCM_PW")
	private String scmPw;

	@Convert(converter = BooleanToYnConverter.class)
	@Column(name = "USE_DEPLOY_PATH")
	private boolean useDeployPath;
	
	@Column(name = "DEPENDENCY_PATH")
	private String dependencyPath;
	
	@Column(name = "DEPLOY_PATH")
	private String deployPath;
	
	@Lob
	@Column(name = "BUILD_SCRIPT")
	private String buildScript;

	@Builder
	public CmpItemDeployEntity (String cmpId, String name, String cmpType, String description
			, String scmType ,String scmUrl ,String scmId ,String scmPw , boolean useDeployPath, String dependencyPath, String deployPath ,String buildScript, String cmpCredential) {
		super(cmpId, name, cmpType, description, cmpCredential);
		
		this.scmType = scmType;
		this.scmUrl = scmUrl;
		this.scmId = scmId;
		this.scmPw = scmPw;
		this.useDeployPath = useDeployPath;
		this.dependencyPath = dependencyPath;
		this.deployPath = deployPath;
		this.buildScript = buildScript;
	}

	public final static String SCM_TYPE = "scmType";
	public final static String SCM_URL = "scmUrl";
	public final static String SCM_ID = "scmId";
	public final static String SCM_PW = "scmPw";
	public final static String DEPENDENCY_PATH = "dependencyPath";
	public final static String DEPLOY_PATH = "deployPath";
	public final static String BUILD_SCRIPT = "buildScript";
}