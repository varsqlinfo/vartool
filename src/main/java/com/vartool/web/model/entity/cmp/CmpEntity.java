package com.vartool.web.model.entity.cmp;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.vartool.web.model.entity.base.AabstractAuditorModel;
import com.vartool.web.model.id.generator.AppUUIDGenerator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@DynamicUpdate
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "CMP_TYPE")
@Table(name = CmpEntity._TB_NAME)
public class CmpEntity extends AabstractAuditorModel{
	public final static String _TB_NAME="VT_CMP";
	
	@Id
	@GenericGenerator(name = "cmpIdGenerator"
		, strategy = "com.vartool.web.model.id.generator.AppUUIDGenerator"
		, parameters = @Parameter(
            name = AppUUIDGenerator.PREFIX_PARAMETER,
            value = ""
		)
	)
    @GeneratedValue(generator = "cmpIdGenerator")
	@Column(name ="CMP_ID")
	private String cmpId; 

	@Column(name ="NAME")
	private String name; 

	@Column(name ="CMP_TYPE", insertable = false, updatable = false)
	private String cmpType; 

	@Column(name ="DESCRIPTION")
	private String description; 
	
	public CmpEntity (String cmpId, String name, String cmpType, String description) {
		this.cmpId = cmpId;
		this.name = name;
		this.cmpType = cmpType;
		this.description = description;
	}

	public final static String CMP_ID="cmpId";
	public final static String NAME="name";
	public final static String CMP_TYPE="cmpType";
	public final static String DESCRIPTION="description";
}