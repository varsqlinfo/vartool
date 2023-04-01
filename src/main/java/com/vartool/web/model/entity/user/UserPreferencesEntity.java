package com.vartool.web.model.entity.user;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.vartool.web.model.entity.base.AbstractAuditorModel;
import com.vartool.web.model.id.UserPreferencesID;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@DynamicUpdate
@NoArgsConstructor
@Entity
@IdClass(UserPreferencesID.class)
@Table(name = UserPreferencesEntity._TB_NAME)
public class UserPreferencesEntity extends AbstractAuditorModel{
	
	private static final long serialVersionUID = 1L;

	public final static String _TB_NAME="VT_PREFERENCES";

	@Id
	private String groupId; 

	@Id
	private String viewid; 

	@Id
	private String prefKey; 

	@Column(name ="PREF_VAL")
	private String prefVal; 

	@Builder
	public UserPreferencesEntity (String groupId ,String viewid ,String prefKey ,String prefVal) {
		this.groupId = groupId;
		this.viewid = viewid;
		this.prefKey = prefKey;
		this.prefVal = prefVal;
	}

	public final static String GROUP_ID="groupId";
	public final static String VIEWID="viewid";
	public final static String PREF_KEY="prefKey";
	public final static String PREF_VAL="prefVal";
}