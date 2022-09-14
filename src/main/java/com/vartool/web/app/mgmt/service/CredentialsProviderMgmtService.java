package com.vartool.web.app.mgmt.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.app.beans.SearchParameter;
import com.vartech.common.utils.StringUtils;
import com.vartool.core.crypto.PasswordCryptionFactory;
import com.vartool.web.dto.CredentialInfo;
import com.vartool.web.dto.request.CredentialsProviderRequestDTO;
import com.vartool.web.dto.response.CredentialsProviderResponseDTO;
import com.vartool.web.exception.ComponentNotFoundException;
import com.vartool.web.model.entity.base.AbstractRegAuditorModel;
import com.vartool.web.model.entity.cmp.CredentialsProviderEntity;
import com.vartool.web.model.mapper.cmp.CredentialsProviderMapper;
import com.vartool.web.module.VartoolBeanUtils;
import com.vartool.web.module.VartoolUtils;
import com.vartool.web.repository.cmp.CredentialsProviderRepository;

import lombok.RequiredArgsConstructor;

/**
 * Credentials Provider service
* 
* @fileName	: CredentialsProviderMgmtService.java
* @author	: ytkim
 */
@Component
@RequiredArgsConstructor
public class CredentialsProviderMgmtService {
	private final static Logger logger = LoggerFactory.getLogger(CredentialsProviderMgmtService.class);
	
	final private CredentialsProviderRepository credentialsProviderRepository;
	
	/**
	 * all Credentials
	 *
	 * @method : all
	 * @param searchParameter
	 * @return
	 */
	public List<CredentialsProviderResponseDTO> allCredentials() {
		List<CredentialsProviderEntity> result = credentialsProviderRepository.findAll(Sort.by(Sort.Direction.ASC, CredentialsProviderEntity.CRED_NAME));
		
		CredentialsProviderMapper instance = CredentialsProviderMapper.INSTANCE;
		
		return result.stream().map(item -> instance.toDto(item)).collect(Collectors.toList());
	}
	
	/**
	 * list
	 *
	 * @method : list
	 * @param searchParameter
	 * @return
	 */
	public ResponseResult list(SearchParameter searchParameter) {
		Sort sort =Sort.by(Sort.Direction.DESC, AbstractRegAuditorModel.REG_DT);
		
		Page<CredentialsProviderEntity> result = credentialsProviderRepository.findAllByCredNameContaining(searchParameter.getKeyword(), VartoolUtils.convertSearchInfoToPage(searchParameter, sort));
		
		return VartoolUtils.getResponseResult(result, searchParameter, CredentialsProviderMapper.INSTANCE);
	}

	/**
	 * 정보 저장.  
	 *
	 * @method : save
	 * @param dto
	 * @return
	 */
	public ResponseResult save(CredentialsProviderRequestDTO dto) {
		CredentialsProviderEntity cpe;
		
		logger.debug("save : {} ", dto);
		
		if(!StringUtils.isBlank(dto.getCredId())) {
			cpe = credentialsProviderRepository.findByCredId(dto.getCredId());
			
			if(cpe == null) {
				throw new ComponentNotFoundException("log component id not found : "+ dto.getCredId());
			}
			
			if(!dto.isChangePassword()) {
				BeanUtils.copyProperties(dto.toEntity(), cpe, "cmpId", "password");
			}else {
				BeanUtils.copyProperties(dto.toEntity(), cpe, "cmpId");
				
				cpe.setPassword(PasswordCryptionFactory.getInstance().encrypt(cpe.getPassword()));
			}
			
		}else {
			cpe = dto.toEntity();
			cpe.setPassword(PasswordCryptionFactory.getInstance().encrypt(cpe.getPassword()));
		}
		
		credentialsProviderRepository.save(cpe);
		
		return VartoolUtils.getResponseResultItemOne(1);
	}

	/**
	 * 정보 삭제. 
	 *
	 * @method : remove
	 * @param cmpId
	 * @return
	 */
	public ResponseResult remove(String credId) {
		CredentialsProviderEntity deleteItem = credentialsProviderRepository.findByCredId(credId);
		
		if(deleteItem == null) {
			throw new ComponentNotFoundException("log component id not found : "+ credId);
		}
		
		credentialsProviderRepository.delete(deleteItem);
		
		return VartoolUtils.getResponseResultItemOne(1);
	}
	
	/**
	 * 정보 복사 
	 *
	 * @method : copyInfo
	 * @param cmpId
	 * @return
	 */
	public ResponseResult copyInfo(String credId) {
		
		CredentialsProviderEntity copyInfo = credentialsProviderRepository.findByCredId(credId);
		
		CredentialsProviderEntity copyEntity = VartoolBeanUtils.copyEntity(copyInfo);
	    copyEntity.setCredId(null);
	    copyEntity.setCredName(copyEntity.getCredName() + "-copy");
	    credentialsProviderRepository.save(copyEntity);
	    
	    return VartoolUtils.getResponseResultItemOne(CredentialsProviderMapper.INSTANCE.toDto(copyEntity));
	}
	
	/**
	 * 인증 정보 얻기
	 *
	 * @method : findCredentialInfo
	 * @param credId
	 * @return
	 */
	public CredentialInfo findCredentialInfo(String credId) {
		return CredentialInfo.toDto(credentialsProviderRepository.findByCredId(credId));
		
	}

}
