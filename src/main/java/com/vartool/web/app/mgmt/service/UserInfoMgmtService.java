package com.vartool.web.app.mgmt.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.app.beans.SearchParameter;
import com.vartech.common.constants.RequestResultCode;
import com.vartech.common.crypto.EncryptDecryptException;
import com.vartech.common.crypto.password.PasswordUtil;
import com.vartech.common.utils.StringUtils;
import com.vartool.core.config.VartoolConfiguration;
import com.vartool.web.constants.ResourceConfigConstants;
import com.vartool.web.constants.VartoolConstants;
import com.vartool.web.dto.response.UserResponseDTO;
import com.vartool.web.exception.ComponentNotFoundException;
import com.vartool.web.exception.VartoolAppException;
import com.vartool.web.model.entity.board.BoardEntity;
import com.vartool.web.model.entity.cmp.CmpGroupUserMappingEntity;
import com.vartool.web.model.entity.user.UserEntity;
import com.vartool.web.module.SecurityUtils;
import com.vartool.web.module.VartoolUtils;
import com.vartool.web.repository.UserMgmtRepository;
import com.vartool.web.repository.cmp.CmpGroupUserMappingRepository;
import com.vartool.web.security.auth.AuthorityType;

/**
 * 사용자 관리
* 
* @fileName	: UserInfoMgmtService.java
* @author	: ytkim
 */
@Component
public class UserInfoMgmtService {
	private final static Logger logger = LoggerFactory.getLogger(UserInfoMgmtService.class);
	
	@Autowired
	private UserMgmtRepository userMgmtRepository; 
	
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
	
		Page<UserEntity> result = null; 
		if(SecurityUtils.isAdmin()) {
			result = userMgmtRepository.findByUserRoleNotAndUnameContaining(AuthorityType.ADMIN.name(), searchParameter.getKeyword(), VartoolUtils.convertSearchInfoToPage(searchParameter, sort));
		}else {
			result = userMgmtRepository.findByUserRoleAndUnameContaining(AuthorityType.USER.name(),searchParameter.getKeyword(), VartoolUtils.convertSearchInfoToPage(searchParameter, sort));
		}
		
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
		UserEntity deleteItem = userMgmtRepository.findByViewid(viewid);
		
		if(deleteItem == null) {
			throw new ComponentNotFoundException("user viewid not found : "+ viewid);
		}
		
		userMgmtRepository.delete(deleteItem);
		
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

	/**
	 *
	 * @Method Name  : updateAccept
	 * @Method 설명 : 사용자 수락 거부 .
	 * @작성자   : ytkim
	 * @작성일   : 2017. 12. 1.
	 * @변경이력  :
	 * @param acceptyn
	 * @param selectItem
	 * @return
	 */
	@Transactional(value=ResourceConfigConstants.APP_TRANSMANAGER, rollbackFor=Exception.class)
	public ResponseResult updateAccept(String acceptyn, String selectItem) {
		String[] viewidArr = StringUtils.split(selectItem,",");
		AuthorityType role = "Y".equals(acceptyn)?AuthorityType.USER:AuthorityType.GUEST;

		List<UserEntity> users= userMgmtRepository.findByViewidIn(Arrays.asList(viewidArr)).stream().map(item -> {
			item.setUserRole(role.name());
			item.setAcceptYn("Y".equals(acceptyn)?true:false);
			return item;
		}).collect(Collectors.toList());

		userMgmtRepository.saveAll(users);

		return VartoolUtils.getResponseResultItemOne(1);
	}
	
	/**
	 *
	 * @Method Name  : initPassword
	 * @Method 설명 : 패스워드 초기화
	 * @작성자   : ytkim
	 * @작성일   : 2017. 12. 1.
	 * @변경이력  :
	 * @param PasswordForm
	 * @return
	 * @throws EncryptDecryptException
	 */
	public ResponseResult initPassword(String viewid) throws EncryptDecryptException {
		
		if(!VartoolConfiguration.getInstance().getPasswordResetMode().equals(VartoolConstants.PASSWORD_RESET_MODE.MANAGER)) {
			throw new VartoolAppException(RequestResultCode.BAD_REQUEST.name());
		}
		
		String passwordInfo = PasswordUtil.createPassword(VartoolConfiguration.getInstance().passwordType(), VartoolConfiguration.getInstance().passwordInitSize());

		UserEntity entity= userMgmtRepository.findByViewid(viewid);
		entity.setUpw(passwordInfo);
		userMgmtRepository.save(entity);

		return VartoolUtils.getResponseResultItemOne(passwordInfo);
	}
}
