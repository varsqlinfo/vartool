package com.vartool.web.dto.response;
import java.time.LocalDateTime;

import com.vartech.common.utils.DateUtils;
import com.vartool.web.model.entity.user.UserEntity;
import com.vartool.web.module.ConvertUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserResponseDTO{
	private String viewid;

	private String uid;

	private String uname;

	private String orgNm;

	private String deptNm;

	private String lang;

	private String uemail;

	private String userRole;

	private String description;

	private boolean acceptYn;

	private boolean blockYn;

	private String regId;

	private String regDt; 
	
	
	public static UserResponseDTO toSimpleDto(UserEntity user) {
		UserResponseDTO dto = new UserResponseDTO();
		dto.setViewid(user.getViewid());
		dto.setUid(user.getUid());
		dto.setUname(user.getUname());
		dto.setRegDt(DateUtils.dateFormat(ConvertUtils.localDateTimeToDate(user.getRegDt())));
		
		return dto; 
	}
	
	public static UserResponseDTO toDto(UserEntity user) {
		UserResponseDTO dto = new UserResponseDTO();
		
		dto.setViewid(user.getViewid());
		dto.setUid(user.getUid());
		dto.setUname(user.getUname());
		dto.setLang(user.getLang());
		dto.setUemail(user.getUemail());
		dto.setOrgNm(user.getOrgNm());
		dto.setDeptNm(user.getDeptNm());
		dto.setAcceptYn(user.isAcceptYn());
		dto.setBlockYn(user.isBlockYn());
		dto.setDescription(user.getDescription());
		dto.setRegDt(DateUtils.dateFormat(ConvertUtils.localDateTimeToDate(user.getRegDt())));
		dto.setAcceptYn(user.isAcceptYn());
		
		return dto; 
	}
}