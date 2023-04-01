package com.vartool.web.model.entity.cmp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.vartool.web.model.entity.base.AbstractAuditorModel;
import com.vartool.web.model.id.generator.AppUUIDGenerator;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@DynamicUpdate
@NoArgsConstructor
@Entity
@Table(name = CredentialsProviderEntity._TB_NAME)
public class CredentialsProviderEntity extends AbstractAuditorModel{

	private static final long serialVersionUID = 1L;

	public final static String _TB_NAME="VT_CREDENTIALS_PROVIDER";
	
	@Id
	@GenericGenerator(name = "credIdGenerator"
		, strategy = "com.vartool.web.model.id.generator.AppUUIDGenerator"
		, parameters = @Parameter(
	        name = AppUUIDGenerator.PREFIX_PARAMETER,
	        value = ""
		)
	)
	@GeneratedValue(generator = "credIdGenerator")
	@Column(name ="CRED_ID")
	private String credId; 

	@Column(name ="CRED_NAME")
	private String credName; 

	@Column(name ="CRED_TYPE")
	private String credType; 

	@Column(name ="USERNAME")
	private String username; 

	@Column(name ="PASSWORD")
	private String password; 

	@Column(name ="SECRET_TEXT")
	private String secretText; 

	@Column(name ="DESCRIPTION")
	private String description; 

	@Builder
	public CredentialsProviderEntity (String credId, String credName, String credType, String username, String password, String secretText, String description) {
		this.credId = credId;
		this.credName = credName;
		this.credType = credType;
		this.username = username;
		this.password = password;
		this.secretText = secretText;
		this.description = description;
	}

	public final static String CRED_ID="credId";
	public final static String CRED_NAME="credName";
	public final static String CRED_TYPE="credType";
	public final static String USERNAME="username";
	public final static String PASSWORD="password";
	public final static String SECRET_TEXT="secretText";
	public final static String DESCRIPTION="description";
}