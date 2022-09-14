package com.vartool.web.dto.request;
import com.vartool.web.model.entity.cmp.CredentialsProviderEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CredentialsProviderRequestDTO {

	private String credId; 
	private String credName; 
	private String credType; 
	private String username; 
	private boolean changePassword; 
	private String password; 
	private String secretText; 
	private String description; 

	@Builder
	public CredentialsProviderRequestDTO (String credId, String credName, String credType, String username, String password, String secretText, String description, String regId) {
		this.credId = credId;
		this.credName = credName;
		this.credType = credType;
		this.username = username;
		this.password = password;
		this.secretText = secretText;
		this.description = description;
	}

	public CredentialsProviderEntity toEntity() {
		return CredentialsProviderEntity.builder()
				.credId(credId)
				.credName(credName)
				.credType(credType)
				.username(username)
				.password(password)
				.secretText(secretText)
				.description(description)
				.build();
	}
	
	public void setChangePassword(String changePassword) {
		this.changePassword = Boolean.valueOf(changePassword);
	}
	
	
}