package com.vartool.web.app.user.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vartool.web.constants.ComponentConstants;
import com.vartool.web.constants.ComponentConstants.TYPE;
import com.vartool.web.constants.PreferencesConstants;
import com.vartool.web.dto.response.CmpGroupCmpMappingResponseDTO;
import com.vartool.web.dto.response.CmpGroupUserMappingResponseDTO;
import com.vartool.web.model.entity.user.UserPreferencesEntity;
import com.vartool.web.module.SecurityUtils;
import com.vartool.web.repository.UserPreferencesRepository;
import com.vartool.web.repository.cmp.CmpGroupCmpMappingRepository;
import com.vartool.web.repository.cmp.CmpGroupRepository;
import com.vartool.web.repository.cmp.CmpGroupUserMappingRepository;

/**
 * user co
* 
* @fileName	: UserCmpService.java
* @author	: ytkim
 */
@Service
public class UserCmpService{
	private final static Logger logger = LoggerFactory.getLogger(UserCmpService.class);
	
	@Autowired
	private CmpGroupRepository cmpGroupRepository; 
	
	@Autowired
	private CmpGroupUserMappingRepository cmpGroupUserMappingRepository; 
	
	@Autowired
	private CmpGroupCmpMappingRepository cmpGroupMappingRepository;
	
	@Autowired
	private UserPreferencesRepository userPreferencesRepository;
	
	/**
	 * user component group list
	 *
	 * @method : allUserGroupList
	 * @return
	 */
	public List<CmpGroupUserMappingResponseDTO> allUserGroupList() {
		if(SecurityUtils.isAdmin()) {
			return cmpGroupRepository.findAllGroup();
		}else {
			return cmpGroupUserMappingRepository.findViewIdMappingInfo(SecurityUtils.userViewId());
		}
	}
	
	/**
	 * user component list
	 *
	 * @method : userCmpInfos
	 * @param cgumr
	 * @return
	 */
	public Map<TYPE, ArrayList> userCmpInfos(CmpGroupUserMappingResponseDTO cgumr) {
		List<CmpGroupCmpMappingResponseDTO> componentList =  cmpGroupMappingRepository.findComponentList(cgumr.getGroupId());
		
		Map<ComponentConstants.TYPE, ArrayList> cmpInfos = new HashMap<ComponentConstants.TYPE, ArrayList>();
		componentList.forEach(item ->{
			ComponentConstants.TYPE cmpType = ComponentConstants.getComponentType(item.getCmpType());
			
			if(cmpType != null) {
				if(!cmpInfos.containsKey(cmpType)) {
					cmpInfos.put(cmpType, new ArrayList());
				}
				
				cmpInfos.get(cmpType).add(item);
			}
		});
		
		return cmpInfos; 
	}
	
	/**
	 * main layout info
	 *
	 * @method : mainLayoutInfo
	 * @param cgumr
	 * @return
	 */
	public String mainLayoutInfo(CmpGroupUserMappingResponseDTO cgumr) {
		UserPreferencesEntity pref = userPreferencesRepository.findByGroupIdAndViewidAndPrefKey(cgumr.getGroupId(), SecurityUtils.userViewId(), PreferencesConstants.PREFKEY.MAIN_LAYOUT.key());
		if(pref== null) {
			return null;
		}else {
			try {
				new ObjectMapper().readTree(pref.getPrefVal()); // json check
				return pref.getPrefVal();
			}catch(Exception e) {
				return null; 
			}
		}
	}

}
