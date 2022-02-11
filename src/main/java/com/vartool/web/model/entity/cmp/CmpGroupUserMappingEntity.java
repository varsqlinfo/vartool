package com.vartool.web.model.entity.cmp;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.vartool.web.model.entity.base.AbstractRegAuditorModel;
import com.vartool.web.model.entity.user.UserEntity;
import com.vartool.web.model.id.CmpGroupIdNCmpIdID;
import com.vartool.web.model.id.CmpGroupIdNViewIdID;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@DynamicUpdate
@NoArgsConstructor
@Entity
@IdClass(CmpGroupIdNViewIdID.class)
@Table(name = CmpGroupUserMappingEntity._TB_NAME)
public class CmpGroupUserMappingEntity extends AbstractRegAuditorModel{
	
	private static final long serialVersionUID = -2688488912328867357L;

	public final static String _TB_NAME="VT_CMP_GROUP_USER_MAPPING";
	
	@Id
	private String groupId; 

	@Id
	private String viewid; 
	
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GROUP_ID", updatable = false, insertable = false)
	private CmpGroupEntity cmpGroupUser;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference
	@JoinColumn(name = "VIEWID", nullable = false, insertable = false, updatable = false)
	private UserEntity userEntity;

	@Builder
	public CmpGroupUserMappingEntity (String groupId ,String viewid) {
		this.groupId = groupId;
		this.viewid = viewid;
	}

	public final static String GROUP_ID="groupId";
	public final static String VIEWID="viewid";
}