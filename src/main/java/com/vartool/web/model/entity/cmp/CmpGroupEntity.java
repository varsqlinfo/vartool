package com.vartool.web.model.entity.cmp;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = CmpGroupEntity._TB_NAME)
public class CmpGroupEntity extends AbstractAuditorModel{
	private static final long serialVersionUID = 1L;

	public final static String _TB_NAME = "VT_CMP_GROUP";

	@Id
	@GenericGenerator(name = "groupIdGenerator"
		, strategy = "com.vartool.web.model.id.generator.AppUUIDGenerator"
		, parameters = @Parameter(
            name = AppUUIDGenerator.PREFIX_PARAMETER,
            value = ""
		)
	)
    @GeneratedValue(generator = "groupIdGenerator")
	@Column(name ="GROUP_ID", nullable = false)
	private String groupId;

	@Column(name = "NAME")
	private String name;

	@Column(name = "DESCRIPTION")
	private String description;
	
	@Column(name ="LAYOUT_INFO")
	private String layoutInfo; 
	
	@JsonManagedReference
	@OneToMany(mappedBy = "cmpGroup", cascade = { CascadeType.REMOVE }, fetch = FetchType.LAZY)
	private Set<CmpGroupMappingEntity> groupMappingInfos;
	
	@JsonManagedReference
	@OneToMany(mappedBy = "cmpGroupUser", cascade = { CascadeType.REMOVE }, fetch = FetchType.LAZY)
	private Set<CmpGroupUserMappingEntity> groupMappingUserInfos;

	@Builder
	public CmpGroupEntity(String groupId ,String name ,String description, String layoutInfo) {
		this.groupId = groupId;
		this.name = name;
		this.description = description;
		this.layoutInfo = layoutInfo;
	}

	public final static String GROUP_ID = "groupId";
	public final static String NAME = "name";
	public final static String LAYOUT_INFO = "layoutInfo";
	public final static String DESCRIPTION = "description";
}