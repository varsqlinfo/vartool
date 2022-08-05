package com.vartool.web.app.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vartech.common.app.beans.ResponseResult;
import com.vartool.web.constants.PreferencesConstants;
import com.vartool.web.dto.request.UserPreferencesRequestDTO;
import com.vartool.web.model.entity.cmp.CmpGroupEntity;
import com.vartool.web.model.entity.user.UserPreferencesEntity;
import com.vartool.web.module.SecurityUtils;
import com.vartool.web.module.VartoolUtils;
import com.vartool.web.repository.UserPreferencesRepository;
import com.vartool.web.repository.cmp.CmpGroupRepository;

/**
 * 사용자 메인 환경설정 service 
* 
* @fileName	: UserMainPrefService.java
* @author	: ytkim
 */
@Service
public class UserMainPrefService{
	private final static Logger logger = LoggerFactory.getLogger(UserMainPrefService.class);
	
	@Autowired
	private UserPreferencesRepository userPreferencesRepository;
	
	@Autowired
	private CmpGroupRepository cmpGroupRepository; 
	
	/**
	 * 저장
	 *
	 * @method : save
	 * @param dto
	 * @return
	 */
	public ResponseResult save(UserPreferencesRequestDTO dto) {
		
		UserPreferencesEntity entity = dto.toEntity(); 
		
		entity.setViewid(SecurityUtils.userViewId());
		
		userPreferencesRepository.save(entity);
		
		return VartoolUtils.getResponseResultItemOne(1);
	}
	
	/**
	 * 기본 레이아웃 지정
	 *
	 * @method : defaultLayout
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
	 * init layout  
	 *
	 * @method : initLayout
	 * @param groupId
	 * @return
	 */
	public ResponseResult initLayout(String groupId) {
		UserPreferencesEntity item = userPreferencesRepository.findByGroupIdAndViewidAndPrefKey(groupId, SecurityUtils.userViewId(), PreferencesConstants.PREFKEY.MAIN_LAYOUT.key());
		
		if(item != null) {
			userPreferencesRepository.delete(item);
		}
		
		return VartoolUtils.getResponseResultItemOne(1);
	}

}
