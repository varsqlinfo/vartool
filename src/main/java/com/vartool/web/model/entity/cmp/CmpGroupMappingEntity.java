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
import com.vartool.web.model.id.CmpGroupIdNCmpIdID;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@DynamicUpdate
@NoArgsConstructor
@Entity
@IdClass(CmpGroupIdNCmpIdID.class)
@Table(name = CmpGroupMappingEntity._TB_NAME)
public class CmpGroupMappingEntity extends AbstractRegAuditorModel{
	
	private static final long serialVersionUID = 3665662410427862588L;

	public final static String _TB_NAME = "VT_CMP_GROUP_MAPPING";

	@Id
	private String groupId;

	@Id
	private String cmpId;
	
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GROUP_ID", updatable = false, insertable = false)
	private CmpGroupEntity cmpGroup;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference
	@JoinColumn(name = "CMP_ID", nullable = false, insertable = false, updatable = false)
	private CmpEntity cmpEntity;

	@Builder
	public CmpGroupMappingEntity(String groupId, String cmpId) {
		this.groupId = groupId;
		this.cmpId = cmpId;
	}

	public final static String GROUP_ID = "groupId";
	public final static String CMP_ID = "cmpId";
}