package com.vartool.web.app.mgmt.service;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.app.beans.SearchParameter;
import com.vartool.web.dto.response.UserResponseDTO;
import com.vartool.web.exception.ComponentNotFoundException;
import com.vartool.web.model.entity.board.BoardEntity;
import com.vartool.web.model.entity.cmp.CmpGroupUserMappingEntity;
import com.vartool.web.model.entity.user.UserEntity;
import com.vartool.web.module.VartoolUtils;
import com.vartool.web.repository.UserInfoRepository;
import com.vartool.web.repository.cmp.CmpGroupUserMappingRepository;
import com.vartool.web.security.auth.AuthorityType;

/**
 * 
*-----------------------------------------------------------------------------
* @PROJECT	: varsql
* @NAME		: UserInfoMgmtService.java
* @DESC		: 사용자 관리
* @AUTHOR	: ytkim
*-----------------------------------------------------------------------------
  DATE			AUTHOR			DESCRIPTION
*-----------------------------------------------------------------------------
* 2020. 2. 6. 			ytkim			최초작성

*-----------------------------------------------------------------------------
 */
@Component
public class UserInfoMgmtService {
	private final static Logger logger = LoggerFactory.getLogger(UserInfoMgmtService.class);
	
	@Autowired
	private UserInfoRepository userInfoRepository; 
	
	@Autowired
	private CmpGroupUserMappingRepository cmpGroupUserMappingRepository; 
	
	/**
	 * 
	 * @Method Name  : list
	 * @Method 설명 : 사용자 list 
	 * @작성자   : ytkim
	 * @작성일   : 2021. 8. 9.
	 * @변경이력  :
	 * @param searchParameter
	 * @return
	 */
	public ResponseResult list(SearchParameter searchParameter) {
		Sort sort =Sort.by(Sort.Direction.DESC, BoardEntity.REG_DT);
		
		Page<UserEntity> result = userInfoRepository.findByUserRoleAndUnameContaining(AuthorityType.USER.name(),searchParameter.getKeyword(), VartoolUtils.convertSearchInfoToPage(searchParameter, sort));
		
		return VartoolUtils.getResponseResult(result.getContent().stream().map(item->{
			 return UserResponseDTO.toDto(item);
		}).collect(Collectors.toList()), result.getTotalElements(), searchParameter);
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
	public ResponseResult remove(String viewid) {
		UserEntity deleteItem = userInfoRepository.findByViewid(viewid);
		
		if(deleteItem == null) {
			throw new ComponentNotFoundException("user viewid not found : "+ viewid);
		}
		
		userInfoRepository.delete(deleteItem);
		
		return VartoolUtils.getResponseResultItemOne(1);
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
	public ResponseResult mappingInfo(String viewid) {
		return VartoolUtils.getResponseResultItemList( cmpGroupUserMappingRepository.findViewIdMappingInfo( viewid));
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
	public ResponseResult modifyUserMappingInfo(String viewid, String groupId, String mode) {
		logger.info("modifyUserMappingInfo  mode :{}, groupId :{} ,selectItem : {} ",  mode, groupId, viewid);
		
		if ("del".equals(mode)) {
			cmpGroupUserMappingRepository.delete(CmpGroupUserMappingEntity.builder().groupId(groupId).viewid(viewid).build());
		} else {
			cmpGroupUserMappingRepository.save(CmpGroupUserMappingEntity.builder().groupId(groupId).viewid(viewid).build());
		}
			
		return VartoolUtils.getResponseResultItemOne(1);
	}
}
