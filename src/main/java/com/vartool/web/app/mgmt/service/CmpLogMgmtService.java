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
import com.vartool.web.dto.request.CmpLogRequestDTO;
import com.vartool.web.dto.response.CmpLogResponseDTO;
import com.vartool.web.exception.ComponentNotFoundException;
import com.vartool.web.model.entity.base.AbstractRegAuditorModel;
import com.vartool.web.model.entity.cmp.CmpItemLogEntity;
import com.vartool.web.module.VartoolBeanUtils;
import com.vartool.web.module.VartoolUtils;
import com.vartool.web.repository.cmp.CmpItemLogRepository;

/**
 * log compoment 관리
* 
* @fileName	: CmpLogMgmtService.java
* @author	: ytkim
 */
@Component
public class CmpLogMgmtService {
	private final static Logger logger = LoggerFactory.getLogger(CmpLogMgmtService.class);
	
	@Autowired
	private CmpItemLogRepository cmpItemLogRepository;
	
	public ResponseResult list(SearchParameter searchParameter) {
		Sort sort =Sort.by(Sort.Direction.DESC, AbstractRegAuditorModel.REG_DT);
		
		Page<CmpItemLogEntity> result = cmpItemLogRepository.findAllByNameContaining(searchParameter.getKeyword(), VartoolUtils.convertSearchInfoToPage(searchParameter, sort));
		
		return VartoolUtils.getResponseResult(result.getContent().stream().map(item->{
			 return CmpLogResponseDTO.toDto(item);
		}).collect(Collectors.toList()), result.getTotalElements(), searchParameter);
	}

	/**
	 * 
	 * @Method Name  : save
	 * @Method 설명 : 정보 저장.  
	 * @작성자   : ytkim
	 * @작성일   : 2021. 7. 28.
	 * @변경이력  :
	 * @param dto
	 * @return
	 */
	public ResponseResult save(CmpLogRequestDTO dto) {
		CmpItemLogEntity cle;
		
		if(!StringUtils.isBlank(dto.getCmpId())) {
			cle = cmpItemLogRepository.findByCmpId(dto.getCmpId());
			
			if(cle == null) {
				throw new ComponentNotFoundException("log component id not found : "+ dto.getCmpId());
			}
			BeanUtils.copyProperties(dto.toEntity(), cle, "cmpId");
		}else {
			cle = dto.toEntity();
		}
		
		cmpItemLogRepository.save(cle);
		
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
		CmpItemLogEntity deleteItem = cmpItemLogRepository.findByCmpId(cmpId);
		
		if(deleteItem == null) {
			throw new ComponentNotFoundException("log component id not found : "+ cmpId);
		}
		
		cmpItemLogRepository.delete(deleteItem);
		
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
		
		CmpItemLogEntity copyInfo = cmpItemLogRepository.findByCmpId(cmpId);
		
		CmpItemLogEntity copyEntity = VartoolBeanUtils.copyEntity(copyInfo);
	    copyEntity.setCmpId(null);
	    copyEntity.setName(copyEntity.getName() + "-copy");
	    cmpItemLogRepository.save(copyEntity);
	    
	    return VartoolUtils.getResponseResultItemOne(CmpLogResponseDTO.toDto(copyEntity));
	}

}
