package com.vartool.web.app.user.service;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vartech.common.app.beans.ResponseResult;
import com.vartool.web.constants.PreferencesConstants;
import com.vartool.web.dto.request.UserPreferencesRequestDTO;
import com.vartool.web.model.entity.cmp.CmpGroupEntity;
import com.vartool.web.model.entity.user.UserPreferencesEntity;
import com.vartool.web.module.SecurityUtil;
import com.vartool.web.module.VartoolUtils;
import com.vartool.web.repository.UserPreferencesRepository;
import com.vartool.web.repository.cmp.CmpGroupRepository;

@Service
public class UserPrefService{
	private final static Logger logger = LoggerFactory.getLogger(UserPrefService.class);
	
	@Autowired
	private UserPreferencesRepository userPreferencesRepository;
	
	@Autowired
	private CmpGroupRepository cmpGroupRepository; 
	
	/**
	 * 
	 * @Method Name  : save
	 * @Method 설명 : 환경설정 저장
	 * @작성자   : ytkim
	 * @작성일   : 2021. 8. 12.
	 * @변경이력  :
	 * @param dto
	 * @return
	 */
	public ResponseResult save(UserPreferencesRequestDTO dto) {
		
		UserPreferencesEntity entity = dto.toEntity(); 
		
		entity.setViewid(SecurityUtil.userViewId());
		
		userPreferencesRepository.save(entity);
		
		return VartoolUtils.getResponseResultItemOne(1);
	}
	
	/**
	 * 
	 * @Method Name  : defaultLayout
	 * @Method 설명 : save group default layout 
	 * @작성자   : ytkim
	 * @작성일   : 2021. 8. 12.
	 * @변경이력  :
	 * @param dto
	 * @return
	 */
	public ResponseResult defaultLayout(UserPreferencesRequestDTO dto) {
		
		CmpGroupEntity entity = cmpGroupRepository.findByGroupId(dto.getGroupId());
		
		if(entity != null) {
			entity.setLayoutInfo(dto.getPrefVal());
			cmpGroupRepository.save(entity);
		}
		
		return VartoolUtils.getResponseResultItemOne(1);
	}
	
	/**
	 * 
	 * @Method Name  : initLayout
	 * @Method 설명 : init layout  
	 * @작성자   : ytkim
	 * @작성일   : 2022. 1. 13.
	 * @변경이력  :
	 * @param groupId
	 * @return
	 */
	public ResponseResult initLayout(String groupId) {
		UserPreferencesEntity item = userPreferencesRepository.findByGroupIdAndViewidAndPrefKey(groupId, SecurityUtil.userViewId(), PreferencesConstants.PREFKEY.MAIN_LAYOUT.key());
		
		if(item != null) {
			userPreferencesRepository.delete(item);
		}
		
		return VartoolUtils.getResponseResultItemOne(1);
	}

}
