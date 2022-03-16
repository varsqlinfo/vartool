package com.vartool.web.dto.response;
import com.vartool.web.model.entity.user.UserEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserResponseDTO{
	private String viewid; 
	private String uid; 
	private String uname; 
	private String lang; 
	private String uemail; 
	private String description;
	
	public static UserResponseDTO toSimpleDto(UserEntity user) {
		UserResponseDTO dto = new UserResponseDTO();
		dto.setViewid(user.getViewid());
		dto.setUid(user.getUid());
		dto.setUname(user.getUname());
		
		return dto; 
	}
	
	public static UserResponseDTO toDto(UserEntity user) {
		UserResponseDTO dto = new UserResponseDTO();
		
		dto.setViewid(user.getViewid());
		dto.setUid(user.getUid());
		dto.setUname(user.getUname());
		dto.setLang(user.getLang());
		dto.setUemail(user.getUemail());
		dto.setDescription(user.getDescription());
		
		return dto; 
	}
}