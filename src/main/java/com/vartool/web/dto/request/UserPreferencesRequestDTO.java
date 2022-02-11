package com.vartool.web.dto.request;
import javax.validation.constraints.NotBlank;

import com.vartool.web.constants.PreferencesConstants;
import com.vartool.web.model.entity.user.UserPreferencesEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserPreferencesRequestDTO {
	
	@NotBlank
	private String groupId; 
	
	@NotBlank
	private String prefKey;
	@NotBlank
	private String prefVal; 

	public UserPreferencesEntity toEntity() {
		return UserPreferencesEntity.builder()
				.groupId(groupId)
				.prefKey(PreferencesConstants.getPrefKey(prefKey))
				.prefVal(prefVal)
			.build();

	}
}