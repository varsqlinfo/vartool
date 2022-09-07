package com.vartool.web.dto.response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CredentialsProviderResponseDTO{

	private String credId; 
	private String credName; 
	private String credType; 
	private String username; 
	private String password; 
	private String secretText; 
	private String description; 

}