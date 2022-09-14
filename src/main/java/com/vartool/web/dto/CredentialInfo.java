package com.vartool.web.dto;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vartool.web.model.entity.cmp.CredentialsProviderEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CredentialInfo{

	private String credId; 
	private String credName; 
	private String credType; 
	private String username; 
	
	@JsonIgnore
	@Transient
	private String password; 
	private String secretText; 
	private String description; 
	
	@Builder
	public CredentialInfo(String credId, String credName, String credType, String username, String password, String secretText, String description) {
		this.credId = credId;
		this.credName = credName;
		this.credType = credType;
		this.username = username;
		this.password = password;
		this.secretText = secretText;
		this.description = description;
	}
	
	public static CredentialInfo toDto(CredentialsProviderEntity entity) {
		return CredentialInfo.builder()
				.credId(entity.getCredId())
				.credName(entity.getCredName())
				.credType(entity.getCredType())
				.username(entity.getUsername())
				.password(entity.getPassword())
				.secretText(entity.getSecretText())
				.description(entity.getDescription())
				.build(); 
	}

}