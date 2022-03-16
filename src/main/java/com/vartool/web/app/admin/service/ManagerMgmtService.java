package com.vartool.web.app.admin.service;

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
import com.vartool.web.dto.response.UserResponseDTO;
import com.vartool.web.model.entity.user.UserEntity;
import com.vartool.web.module.VartoolUtils;
import com.vartool.web.repository.UserInfoRepository;
import com.vartool.web.security.auth.AuthorityType;

/**
 * 
 * -----------------------------------------------------------------------------
 * 
 * @PROJECT : varsql
 * @NAME : UserInfoMgmtService.java
 * @DESC : 사용자 관리
 * @AUTHOR : ytkim
 *         -----------------------------------------------------------------------------
 *         DATE AUTHOR DESCRIPTION
 *         -----------------------------------------------------------------------------
 *         2020. 2. 6. ytkim 최초작성
 * 
 *         -----------------------------------------------------------------------------
 */
@Component
public class ManagerMgmtService {
	private final static Logger logger = LoggerFactory.getLogger(ManagerMgmtService.class);

	@Autowired
	private UserInfoRepository userInfoRepository;

	public ResponseResult searchRoleUserList(AuthorityType auth, SearchParameter searchParameter) {
		Sort sort = Sort.by(Sort.Direction.DESC, UserEntity.UNAME);

		Page<UserEntity> result = this.userInfoRepository.findByUserRoleAndUnameContaining(auth.name(),
				searchParameter.getKeyword(), VartoolUtils.convertSearchInfoToPage(searchParameter, sort));

		return VartoolUtils.getResponseResult(result.getContent().stream().map(item -> {
			return UserResponseDTO.toDto(item);
		}).collect(Collectors.toList()), result.getTotalElements(), searchParameter);
	}

	@Transactional(value = "transactionManager", rollbackFor = { Exception.class })
	public ResponseResult updateManagerRole(String mode, String viewid) {
		logger.info("updateManagerRole  mode : {} , viewid : {} ", mode, viewid);
		UserEntity userInfo = this.userInfoRepository.findByViewid(viewid);
		userInfo.setUserRole("add".equals(mode) ? AuthorityType.MANAGER.name() : AuthorityType.USER.name());
		userInfo = this.userInfoRepository.save(userInfo);

		return VartoolUtils.getResponseResultItemOne(1);
	}

}
