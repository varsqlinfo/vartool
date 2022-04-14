package com.vartool.web.app.mgmt.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.app.beans.SearchParameter;
import com.vartool.web.app.handler.deploy.git.GitSource;
import com.vartool.web.dto.request.CmpDeployRequestDTO;
import com.vartool.web.dto.response.CmpDeployResponseDTO;
import com.vartool.web.exception.ComponentNotFoundException;
import com.vartool.web.model.entity.base.AbstractRegAuditorModel;
import com.vartool.web.model.entity.cmp.CmpItemDeployEntity;
import com.vartool.web.module.VarsqlBeanUtils;
import com.vartool.web.module.VartoolUtils;
import com.vartool.web.repository.cmp.CmpItemDeployRepository;

/**
 * 
*-----------------------------------------------------------------------------
* @PROJECT	: varsql
* @NAME		: MgmtService.java
* @DESC		: 설정 정보 처리. 
* @AUTHOR	: ytkim
*-----------------------------------------------------------------------------
  DATE			AUTHOR			DESCRIPTION
*-----------------------------------------------------------------------------
* 2020. 2. 6. 			ytkim			최초작성

*-----------------------------------------------------------------------------
 */
@Component
public class CmpDeployMgmtService {
	private final static Logger logger = LoggerFactory.getLogger(CmpDeployMgmtService.class);
	
	@Autowired
	private CmpItemDeployRepository cmpItemDeployRepository;
	
	public ResponseResult list(SearchParameter searchParameter) {
		Sort sort =Sort.by(Sort.Direction.DESC, AbstractRegAuditorModel.REG_DT);
		
		Page<CmpItemDeployEntity> result = cmpItemDeployRepository.findAllByNameContaining(searchParameter.getKeyword(), VartoolUtils.convertSearchInfoToPage(searchParameter, sort));
		
		return VartoolUtils.getResponseResult(result.getContent().stream().map(item->{
			 return CmpDeployResponseDTO.toDto(item);
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
	public ResponseResult save(CmpDeployRequestDTO dto) {
		
		CmpItemDeployEntity entity;
		
		if(!StringUtils.isBlank(dto.getCmpId())) {
			entity = cmpItemDeployRepository.findByCmpId(dto.getCmpId());
			
			if(entity == null) {
				throw new ComponentNotFoundException("deploy component id not found : "+ dto.getCmpId());
			}
			
			BeanUtils.copyProperties(dto.toEntity(), entity, "cmpId");
			
		}else {
			entity = dto.toEntity();
		}
		
		cmpItemDeployRepository.save(entity);
		
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
		CmpItemDeployEntity deleteItem = cmpItemDeployRepository.findByCmpId(cmpId);
		
		if(deleteItem == null) {
			throw new ComponentNotFoundException("deploy component id not found : "+ cmpId);
		}
		
		cmpItemDeployRepository.delete(deleteItem);
		
		return VartoolUtils.getResponseResultItemOne(1);
	}
	
	
	public ResponseResult connChk(CmpDeployRequestDTO dto) {
		ResponseResult result = new ResponseResult();
		result.setItemOne( new GitSource(null, null, null).checkGitRepo( dto.getScmUrl(),  dto.getScmId(),  dto.getScmPw()));
		
		return result;
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
		
		CmpItemDeployEntity copyInfo = cmpItemDeployRepository.findByCmpId(cmpId);
		
		CmpItemDeployEntity copyEntity = VarsqlBeanUtils.copyEntity(copyInfo);
	    copyEntity.setCmpId(null);
	    copyEntity.setName(copyEntity.getName() + "-copy");
	    cmpItemDeployRepository.save(copyEntity);
	    
	    return VartoolUtils.getResponseResultItemOne(CmpDeployResponseDTO.toDto(copyEntity));
	}

	public ResponseResult removeDeployDir(String cmpId, String mode) throws IOException {
		CmpItemDeployEntity dto = cmpItemDeployRepository.findByCmpId(cmpId);
		
		File file = null; 
		if("all".equals(mode)) {
			file = new File(dto.getDeployPath());
		}else if("class".equals(mode)) {
			file = new File(dto.getDeployPath()+"/WEB-INF/classes");
		}
		
		if(file != null && file.exists()) {
			Files.walk(file.toPath())
		    .sorted(Comparator.reverseOrder())
		    .map(Path::toFile)
		    .forEach(File::delete);
		}
		
		return VartoolUtils.getResponseResultItemOne(1);
	}

}
