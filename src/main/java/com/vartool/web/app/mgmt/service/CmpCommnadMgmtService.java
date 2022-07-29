package com.vartool.web.app.mgmt.service;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.app.beans.SearchParameter;
import com.vartech.common.utils.StringUtils;
import com.vartool.web.dto.request.CmpCommandRequestDTO;
import com.vartool.web.dto.response.CmpCommandResponseDTO;
import com.vartool.web.exception.ComponentNotFoundException;
import com.vartool.web.model.entity.base.AbstractRegAuditorModel;
import com.vartool.web.model.entity.cmp.CmpItemCommandEntity;
import com.vartool.web.module.VartoolBeanUtils;
import com.vartool.web.module.VartoolUtils;
import com.vartool.web.repository.cmp.CmpItemCommandRepository;

/**
 * 
*-----------------------------------------------------------------------------
* 
* @NAME		: CmpCommnadMgmtService.java
* @DESC		: command component관리
* @AUTHOR	: ytkim
*-----------------------------------------------------------------------------
  DATE			AUTHOR			DESCRIPTION
*-----------------------------------------------------------------------------
* 2020. 2. 6. 			ytkim			최초작성

*-----------------------------------------------------------------------------
 */
@Component
public class CmpCommnadMgmtService {
	private final static Logger logger = LoggerFactory.getLogger(CmpCommnadMgmtService.class);
	
	@Autowired
	private CmpItemCommandRepository cmpItemCommandRepository; 
	
	public ResponseResult list(SearchParameter searchParameter) {
		Sort sort =Sort.by(Sort.Direction.DESC, AbstractRegAuditorModel.REG_DT);
		
		Page<CmpItemCommandEntity> result = cmpItemCommandRepository.findAllByNameContaining(searchParameter.getKeyword(), VartoolUtils.convertSearchInfoToPage(searchParameter, sort));
		
		return VartoolUtils.getResponseResult(result.getContent().stream().map(item->{
			 return CmpCommandResponseDTO.toDto(item);
		}).collect(Collectors.toList()), result.getTotalElements(), searchParameter);
	}

	/**
	 * 
	 * @Method Name  : save
	 * @Method 설명 : command 정보 저장. 
	 * @작성자   : ytkim
	 * @작성일   : 2021. 7. 28.
	 * @변경이력  :
	 * @param dto
	 * @return
	 */
	public ResponseResult save(CmpCommandRequestDTO dto) {
		
		CmpItemCommandEntity cce;
		
		if(!StringUtils.isBlank(dto.getCmpId())) {
			cce = cmpItemCommandRepository.findByCmpId(dto.getCmpId());
			
			if(cce == null) {
				throw new ComponentNotFoundException("command component id not found : "+ dto.getCmpId());
			}
			
			BeanUtils.copyProperties(dto.toEntity(), cce, "cmpId");
			
		}else {
			cce = dto.toEntity();
		}
		
		cmpItemCommandRepository.save(cce);
		
		return VartoolUtils.getResponseResultItemOne(1);
	}

	/**
	 * 
	 * @Method Name  : remove
	 * @Method 설명 : 정보 삭제. 
	 * @작성자   : ytkim
	 * @작성일   : 2021. 7. 28.
	 * @변경이력  :
	 * @param cmpId
	 * @return
	 */
	public ResponseResult remove(String cmpId) {
		CmpItemCommandEntity deleteItem = cmpItemCommandRepository.findByCmpId(cmpId);
		
		if(deleteItem == null) {
			throw new ComponentNotFoundException("command component id not found : "+ cmpId);
		}
		
		cmpItemCommandRepository.delete(deleteItem);
		
		return VartoolUtils.getResponseResultItemOne(1);
	}
	
	/**
	 * 
	 * @Method Name  : copyInfo
	 * @Method 설명 : 정보 복사 
	 * @작성자   : ytkim
	 * @작성일   : 2021. 8. 17.
	 * @변경이력  :
	 * @param cmpId
	 * @return
	 */
	public ResponseResult copyInfo(String cmpId) {
		
		CmpItemCommandEntity copyInfo = cmpItemCommandRepository.findByCmpId(cmpId);
		
		CmpItemCommandEntity copyEntity = VartoolBeanUtils.copyEntity(copyInfo);
	    copyEntity.setCmpId(null);
	    copyEntity.setName(copyEntity.getName() + "-copy");
	    cmpItemCommandRepository.save(copyEntity);
	    
	    return VartoolUtils.getResponseResultItemOne(CmpCommandResponseDTO.toDto(copyEntity));
	}
}
