package com.vartool.web.dto.request;

import com.vartool.web.model.entity.cmp.CmpItemDeployEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CmpDeployRequestDTO extends CmpRequestDTO {
	private String scmType;
	private String scmUrl;
	private String scmId;
	private String scmPw;
	private boolean useDeployPath;
	private String dependencyPath;
	private String deployPath;
	private String buildScript;
	
	public CmpItemDeployEntity toEntity() {
		return CmpItemDeployEntity.builder()
			.name(getName())
			.cmpId(getCmpId())
			.description(getDescription())
			.scmType(scmType)
			.scmUrl(scmUrl)
			.scmId(scmId)
			.scmPw(scmPw)
			.useDeployPath(useDeployPath)
			.dependencyPath(dependencyPath)
			.deployPath(deployPath)
			.buildScript(buildScript)
			.build();
	}
}