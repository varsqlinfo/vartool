package com.vartool.web.model.id;

import java.io.Serializable;

import javax.persistence.Column;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of= {"groupId","viewid", "prefKey"})
public class UserPreferencesID implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name ="GROUP_ID")
	private String groupId;

	@Column(name ="VIEWID")
	private String viewid;
	
	@Column(name ="PREF_KEY")
	private String prefKey; 

}
