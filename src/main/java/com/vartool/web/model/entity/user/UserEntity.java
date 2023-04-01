package com.vartool.web.model.entity.user;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vartool.web.model.converter.BooleanToYnConverter;
import com.vartool.web.model.converter.PasswordEncodeConverter;
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
@Table(name = UserEntity._TB_NAME)
public class UserEntity extends AbstractAuditorModel{
	private static final long serialVersionUID = 1L;

	public final static String _TB_NAME="VT_USER";

	@Id
	@GenericGenerator(name = "viewidGenerator"
		, strategy = "com.vartool.web.model.id.generator.AppUUIDGenerator"
		, parameters = @Parameter(
            name = AppUUIDGenerator.PREFIX_PARAMETER,
            value = "U_"
		)
	)
    @GeneratedValue(generator = "viewidGenerator")
	@Column(name ="VIEWID")
	private String viewid;

	@Column(name ="UID")
	private String uid;

	@JsonIgnore
	@Convert(converter = PasswordEncodeConverter.class)
	@Column(name ="UPW")
	private String upw;

	@Column(name ="UNAME")
	private String uname;

	@Column(name ="LANG")
	private String lang;

	@Column(name ="UEMAIL")
	private String uemail;

	@Column(name ="USER_ROLE")
	private String userRole;
	
	@Column(name ="ORG_NM")
	private String orgNm;
	
	@Column(name ="DEPT_NM")
	private String deptNm;

	@Column(name ="DESCRIPTION")
	private String description;

	@Column(name ="ACCEPT_YN")
	@Convert(converter = BooleanToYnConverter.class)
	private boolean acceptYn;

	@Column(name ="BLOCK_YN")
	@Convert(converter = BooleanToYnConverter.class)
	private boolean blockYn;

	@Builder
	public UserEntity(String viewid, String uid, String upw, String uname, String lang, String uemail, String userRole, String description, String orgNm, String deptNm, boolean acceptYn, boolean blockYn) {
		this.viewid = viewid;
		this.uid = uid;
		this.upw = upw;
		this.uname = uname;
		this.lang = lang;
		this.uemail = uemail;
		this.userRole = userRole;
		this.description = description;
		this.orgNm = orgNm;
		this.deptNm = deptNm;
		this.acceptYn = acceptYn;
		this.blockYn = blockYn;

	}

	public final static String VIEWID="viewid";

	public final static String UID="uid";

	public final static String UPW="upw";

	public final static String UNAME="uname";

	public final static String LANG="lang";

	public final static String UEMAIL="uemail";

	public final static String USER_ROLE="userRole";

	public final static String DESCRIPTION="description";
	
	public final static String ORG_NM="orgNm";
	
	public final static String DEPT_NM="deptNm";

	public final static String ACCEPT_YN="acceptYn";

	public final static String BLOCK_YN="blockYn";
}
