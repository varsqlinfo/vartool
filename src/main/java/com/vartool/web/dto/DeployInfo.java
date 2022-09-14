package com.vartool.web.dto;

import com.vartool.web.model.entity.cmp.CmpItemDeployEntity;
import com.vartool.web.model.entity.cmp.CredentialsProviderEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeployInfo{
	
	private String cmpId;
	private String name;
	private String cmpType;
	private String description;
	
	private String scmType;
	private String scmUrl;
	private String scmId;
	private String scmPw;
	private String dependencyPath;
	private String deployPath;
	private String buildScript;
	
	// 추가 적인 정보
	private String action;
	
	public static DeployInfo toDto(CmpItemDeployEntity entity, CredentialInfo credentialInfo) {
		DeployInfo dto= new DeployInfo();
		
		dto.setName(entity.getName());
		dto.setCmpId(entity.getCmpId());
		dto.setCmpType(entity.getCmpType());
		dto.setDescription(entity.getDescription());
		
		dto.setScmType(entity.getScmType());
		dto.setScmUrl(entity.getScmUrl());
		dto.setScmId(credentialInfo.getUsername());
		dto.setScmPw(credentialInfo.getPassword());
		dto.setDependencyPath(entity.getDependencyPath());
		dto.setDeployPath(entity.getDeployPath());
		dto.setBuildScript(entity.getBuildScript());
		
		return dto;
	}
}