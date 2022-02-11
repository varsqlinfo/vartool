package com.vartool.web.dto.request;

import com.vartool.web.model.entity.cmp.CmpGroupEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CmpGroupRequestDTO{
	
	private String groupId;

	private String name;

	private String description;
	
	private String layoutInfo; 
	
	public CmpGroupRequestDTO(String groupId ,String name ,String description, String layoutInfo) {
		this.groupId = groupId;
		this.name = name;
		this.description = description;
		this.layoutInfo = layoutInfo;
	}
	
	public CmpGroupEntity toEntity() {
		return CmpGroupEntity.builder()
				.groupId(groupId)
				.name(name)
				.description(description)
				.layoutInfo(layoutInfo)
				.build();

	}
}