package com.vartool.web.app.mgmt.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.app.beans.SearchParameter;
import com.vartech.common.utils.StringUtils;
import com.vartool.web.dto.request.CredentialsProviderRequestDTO;
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
		
		if(!StringUtils.isBlank(dto.getCredId())) {
			cpe = credentialsProviderRepository.findByCredId(dto.getCredId());
			
			if(cpe == null) {
				throw new ComponentNotFoundException("log component id not found : "+ dto.getCredId());
			}
			BeanUtils.copyProperties(dto.toEntity(), cpe, "cmpId");
		}else {
			cpe = dto.toEntity();
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
	public ResponseResult remove(String cmpId) {
		CredentialsProviderEntity deleteItem = credentialsProviderRepository.findByCredId(cmpId);
		
		if(deleteItem == null) {
			throw new ComponentNotFoundException("log component id not found : "+ cmpId);
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
	public ResponseResult copyInfo(String cmpId) {
		
		CredentialsProviderEntity copyInfo = credentialsProviderRepository.findByCredId(cmpId);
		
		CredentialsProviderEntity copyEntity = VartoolBeanUtils.copyEntity(copyInfo);
	    copyEntity.setCredId(null);
	    copyEntity.setCredName(copyEntity.getCredName() + "-copy");
	    credentialsProviderRepository.save(copyEntity);
	    
	    return VartoolUtils.getResponseResultItemOne(CredentialsProviderMapper.INSTANCE.toDto(copyEntity));
	}

}
