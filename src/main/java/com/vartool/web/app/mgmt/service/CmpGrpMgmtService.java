package com.vartool.web.app.mgmt.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.app.beans.SearchParameter;
import com.vartool.web.constants.ResourceConfigConstants;
import com.vartool.web.dto.request.CmpGroupRequestDTO;
import com.vartool.web.dto.response.UserResponseDTO;
import com.vartool.web.exception.ComponentNotFoundException;
import com.vartool.web.model.entity.board.BoardEntity;
import com.vartool.web.model.entity.cmp.CmpEntity;
import com.vartool.web.model.entity.cmp.CmpGroupEntity;
import com.vartool.web.model.entity.cmp.CmpGroupMappingEntity;
import com.vartool.web.model.entity.cmp.CmpGroupUserMappingEntity;
import com.vartool.web.model.entity.user.UserEntity;
import com.vartool.web.module.VartoolUtils;
import com.vartool.web.repository.UserMgmtRepository;
import com.vartool.web.repository.cmp.CmpGroupCmpMappingRepository;
import com.vartool.web.repository.cmp.CmpGroupRepository;
import com.vartool.web.repository.cmp.CmpGroupUserMappingRepository;
import com.vartool.web.repository.cmp.CmpRepository;

/**
 * component group 관리
* 
* @fileName	: CmpGrpMgmtService.java
* @author	: ytkim
 */
@Component
public class CmpGrpMgmtService {
	private final static Logger logger = LoggerFactory.getLogger(CmpGrpMgmtService.class);
	
	@Autowired
	private CmpGroupRepository cmpGroupRepository; 
	
	@Autowired
	private CmpRepository cmpRepository;
	
	@Autowired
	private UserMgmtRepository userInfoRepository; 
	
	@Autowired
	private CmpGroupCmpMappingRepository cmpGroupMappingRepository;
	
	@Autowired
	private CmpGroupUserMappingRepository cmpGroupUserMappingRepository; 
	
	/**
	 * 
	 * @Method Name  : list
	 * @Method 설명 : 그룹 정보 리스트. 
	 * @작성자   : ytkim
	 * @작성일   : 2021. 8. 9.
	 * @변경이력  :
	 * @param searchParameter
	 * @return
	 */
	public ResponseResult list(SearchParameter searchParameter) {
		Sort sort =Sort.by(Sort.Direction.DESC, BoardEntity.REG_DT);
		
		Page<CmpGroupEntity> result = cmpGroupRepository.findByNameContaining(searchParameter.getKeyword(),VartoolUtils.convertSearchInfoToPage(searchParameter, sort));
		
		return VartoolUtils.getResponseResult(result.getContent().stream().map(item->{
			 return item;
		}).collect(Collectors.toList()), result.getTotalElements(), searchParameter);
	}

	/**
	 * 
	 * @Method Name  : save
	 * @Method 설명 : 정보 저장. 
	 * @작성자   : ytkim
	 * @작성일   : 2021. 8. 5.
	 * @변경이력  :
	 * @param dto
	 * @return
	 */
	public ResponseResult save(CmpGroupRequestDTO dto) {
		CmpGroupEntity entity;
		
		if(!StringUtils.isBlank(dto.getGroupId())) {
			entity = cmpGroupRepository.findByGroupId(dto.getGroupId());
			
			if(entity == null) {
				throw new ComponentNotFoundException("component group id not found : "+ dto.getGroupId());
			}
			
			BeanUtils.copyProperties(dto.toEntity(), entity, "groupId");
			
		}else {
			entity = dto.toEntity();
		}
		
		cmpGroupRepository.save(entity);
		
		return VartoolUtils.getResponseResultItemOne(1);
	}

	/**
	 * 
	 * @Method Name  : remove
	 * @Method 설명 :  정보 삭제.  
	 * @작성자   : ytkim
	 * @작성일   : 2021. 4. 8.
	 * @변경이력  :
	 * @param uid
	 * @return
	 */
	public ResponseResult remove(String groupId) {
		CmpGroupEntity deleteItem = cmpGroupRepository.findByGroupId(groupId);
		
		if(deleteItem == null) {
			throw new ComponentNotFoundException("component group id not found : "+ groupId);
		}
		
		cmpGroupRepository.delete(deleteItem);
		
		return VartoolUtils.getResponseResultItemOne(1);
	}
	
	/**
	 * 
	 * @Method Name  : cmpList
	 * @Method 설명 : component list 
	 * @작성자   : ytkim
	 * @작성일   : 2021. 8. 6.
	 * @변경이력  :
	 * @param searchParameter
	 * @return
	 */
	public ResponseResult cmpList(SearchParameter searchParameter) {
		return VartoolUtils.getResponseResultItemList( cmpRepository.findAllByNameContaining(Sort.by(Sort.Direction.ASC, CmpEntity.NAME)));
	}
	
	/**
	 * 
	 * @Method Name  : mappingInfo
	 * @Method 설명 : 그룹 component 맵핑정보 
	 * @작성자   : ytkim
	 * @작성일   : 2021. 8. 9.
	 * @변경이력  :
	 * @param groupId
	 * @return
	 */
	public ResponseResult mappingInfo(String groupId) {
		return VartoolUtils.getResponseResultItemList( cmpGroupMappingRepository.findMappingInfo( groupId));
	}
	
	/**
	 * 
	 * @Method Name  : modifyMappingInfo
	 * @Method 설명 : component mapping info modify  
	 * @작성자   : ytkim
	 * @작성일   : 2021. 8. 9.
	 * @변경이력  :
	 * @param selectItem
	 * @param groupId
	 * @param mode
	 * @return
	 */
	@Transactional(value = ResourceConfigConstants.APP_TRANSMANAGER, rollbackFor = {Throwable.class})
	public ResponseResult modifyMappingInfo(String selectItem, String groupId, String mode) {
		logger.info("modifyMappingInfo  mode :{}, groupId :{} ,selectItem : {} ",  mode, groupId, selectItem);
		
		String[] vconnidArr = StringUtils.split(selectItem, ",");
		List<CmpGroupMappingEntity> dbConnList = new ArrayList<>();
		for (String id : vconnidArr)
			dbConnList.add(CmpGroupMappingEntity.builder().groupId(groupId).cmpId(id).build());
		int result = 0;
		if (dbConnList.size() > 0) {
			if ("del".equals(mode)) {
				cmpGroupMappingRepository.deleteAll(dbConnList);
			} else {
				cmpGroupMappingRepository.saveAll(dbConnList);
			}
			result = 1;
		}
		return VartoolUtils.getResponseResultItemOne(result);
	}
	
	/**
	 * 
	 * @Method Name  : userList
	 * @Method 설명 : user info list
	 * @작성자   : ytkim
	 * @작성일   : 2021. 8. 9.
	 * @변경이력  :
	 * @param searchParameter
	 * @return
	 */
	public ResponseResult userList(SearchParameter searchParameter) {
		return VartoolUtils.getResponseResultItemList( userInfoRepository.findAll(Sort.by(Sort.Direction.ASC, UserEntity.UNAME)).stream().map(item->{
			return UserResponseDTO.toSimpleDto(item);
		}).collect(Collectors.toList()));
	}
	
	/**
	 * 
	 * @Method Name  : userMappingInfo
	 * @Method 설명 : Group & user mapping list
	 * @작성자   : ytkim
	 * @작성일   : 2021. 8. 9.
	 * @변경이력  :
	 * @param groupId
	 * @return
	 */
	public ResponseResult userMappingInfo(String groupId) {
		return VartoolUtils.getResponseResultItemList( cmpGroupUserMappingRepository.findGroupUserMappingInfo(groupId) );
	}

	/**
	 * 
	 * @Method Name  : modifyUserMappingInfo
	 * @Method 설명 : 사용자 그룹 맵핑 정보 
	 * @작성자   : ytkim
	 * @작성일   : 2021. 8. 9.
	 * @변경이력  :
	 * @param selectItem
	 * @param groupId
	 * @param mode
	 * @return
	 */
	public ResponseResult modifyUserMappingInfo(String selectItem, String groupId, String mode) {
		logger.info("modifyUserMappingInfo  mode :{}, groupId :{} ,selectItem : {} ",  mode, groupId, selectItem);
		
		String[] idArr = StringUtils.split(selectItem, ",");
		List<CmpGroupUserMappingEntity> userMappingList = new ArrayList<>();
		for (String id : idArr)
			userMappingList.add(CmpGroupUserMappingEntity.builder().groupId(groupId).viewid(id).build());
		int result = 0;
		if (userMappingList.size() > 0) {
			if ("del".equals(mode)) {
				cmpGroupUserMappingRepository.deleteAll(userMappingList);
			} else {
				cmpGroupUserMappingRepository.saveAll(userMappingList);
			}
			result = 1;
		}
		return VartoolUtils.getResponseResultItemOne(result);
	}
}
