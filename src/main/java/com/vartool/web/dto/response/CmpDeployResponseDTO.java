package com.vartool.web.dto.response;

import com.vartool.web.model.entity.cmp.CmpItemDeployEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CmpDeployResponseDTO extends CmpResponseDTO {
	private String scmType;
	private String scmUrl;
	private String scmId;
	private String scmPw;
	private boolean useDeployPath; 
	private String dependencyPath;
	private String deployPath;
	private String buildScript;
	
	// 추가 적인 정보
	private String action;
	private String ip;
	
	public static CmpDeployResponseDTO toDto(CmpItemDeployEntity entity) {
		CmpDeployResponseDTO dto= new CmpDeployResponseDTO();
		
		dto.setName(entity.getName());
		dto.setCmpId(entity.getCmpId());
		dto.setCmpType(entity.getCmpType());
		dto.setDescription(entity.getDescription());
		dto.setRegDt(entity.getRegDt());
		
		dto.setScmType(entity.getScmType());
		dto.setScmUrl(entity.getScmUrl());
		dto.setScmId(entity.getScmId());
		dto.setScmPw(entity.getScmPw());
		dto.setUseDeployPath(entity.isUseDeployPath());
		dto.setDependencyPath(entity.getDependencyPath());
		dto.setDeployPath(entity.getDeployPath());
		dto.setBuildScript(entity.getBuildScript());
		
		return dto;
	}
}