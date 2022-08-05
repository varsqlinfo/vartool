package com.vartool.web.app.mgmt.service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.app.beans.SearchParameter;
import com.vartech.common.constants.RequestResultCode;
import com.vartech.common.crypto.EncryptDecryptException;
import com.vartech.common.utils.StringUtils;
import com.vartool.core.crypto.PasswordCryptionFactory;
import com.vartool.web.app.handler.deploy.git.GitSource;
import com.vartool.web.constants.ResourceConfigConstants;
import com.vartool.web.dto.request.CmpDeployRequestDTO;
import com.vartool.web.dto.response.CmpDeployResponseDTO;
import com.vartool.web.exception.ComponentNotFoundException;
import com.vartool.web.exception.VartoolAppException;
import com.vartool.web.model.entity.base.AbstractRegAuditorModel;
import com.vartool.web.model.entity.cmp.CmpItemDeployEntity;
import com.vartool.web.module.SecurityUtil;
import com.vartool.web.module.VartoolBeanUtils;
import com.vartool.web.module.VartoolUtils;
import com.vartool.web.repository.cmp.CmpItemDeployRepository;
import com.vartool.web.security.UserService;

/**
 * 
*-----------------------------------------------------------------------------
* 
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
	
	@Autowired
	@Qualifier(ResourceConfigConstants.USER_DETAIL_SERVICE)
	private UserService userService;
	
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
	 * @throws EncryptDecryptException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws BeansException 
	 */
	public ResponseResult save(CmpDeployRequestDTO dto) throws EncryptDecryptException, BeansException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		CmpItemDeployEntity entity;
		
		if(!StringUtils.isBlank(dto.getCmpId())) {
			CmpItemDeployEntity currentEntity = cmpItemDeployRepository.findByCmpId(dto.getCmpId());
			
			if(currentEntity == null) {
				throw new ComponentNotFoundException("deploy component id not found : "+ dto.getCmpId());
			}
			
			entity = new CmpItemDeployEntity();
			BeanUtils.copyProperties(currentEntity, entity);
		
			copyNonNullProperties(dto.toEntity(), entity, new String[] {  "cmpId","scmPw" });
			
			if (dto.isPasswordChange()) {
				entity.setScmPw(dto.getScmPw());
			}
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
	
	public ResponseResult connChk(CmpDeployRequestDTO dto) throws EncryptDecryptException {
		ResponseResult result = new ResponseResult();
		
		if(StringUtils.isBlank(dto.getScmPw()) && !StringUtils.isBlank(dto.getCmpId())) {
			
			CmpItemDeployEntity cde = cmpItemDeployRepository.findByCmpId(dto.getCmpId());
			
			result.setItemOne( new GitSource(null, null, null).checkGitRepo(dto.getScmUrl(), dto.getScmId(), PasswordCryptionFactory.getInstance().decrypt(cde.getScmPw())));
		}else {
			result.setItemOne( new GitSource(null, null, null).checkGitRepo(dto.getScmUrl(), dto.getScmId(), dto.getScmPw()));
		}
		
		
		
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
		
		CmpItemDeployEntity copyEntity = VartoolBeanUtils.copyEntity(copyInfo);
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
	
	/**
	 * password 보기.
	 * @param cmpId
	 * @param userPw
	 * @return
	 */
	public ResponseResult viewPwInfo(String cmpId, String userPw) {
		ResponseResult resultObject = new ResponseResult();
		if(!userService.passwordCheck(SecurityUtil.loginName(), userPw)) {
			resultObject.setResultCode(RequestResultCode.ERROR);
			resultObject.setMessage("password not valid");
			return resultObject;
		}
		
		CmpItemDeployEntity dto = cmpItemDeployRepository.findByCmpId(cmpId);

		if(dto == null) {
			throw new VartoolAppException("Deploy Component not found");
		}
		try {
			resultObject.setItemOne(PasswordCryptionFactory.getInstance().decrypt(dto.getScmPw()));
		}catch(EncryptDecryptException e) {
			resultObject.setItemOne("password decrypt error");
		}
		return resultObject;
	}
	
	
	public static void copyNonNullProperties(Object src, Object target, String... checkProperty) throws BeansException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
	    BeanUtils.copyProperties(src, target, getNullPropertyNames(src, checkProperty));
	}

	public static String[] getNullPropertyNames (Object source, String[] checkProperty) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
	    final BeanWrapper src = new BeanWrapperImpl(source);
	    java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
	    List<String> prop = Arrays.asList(checkProperty);
	    Set<String> emptyNames = new HashSet<String>();
	    for(java.beans.PropertyDescriptor pd : pds) {
	    	String name = pd.getName();

	    	if(prop.contains(name)) {
	    		Object srcValue = src.getPropertyValue(pd.getName());
		        if (srcValue == null || "".equals(srcValue)) {
		        	emptyNames.add(name);
		        }else {
		        	if("".equals(srcValue.toString().trim())) {
		        		PropertyUtils.setProperty(source, name, srcValue.toString().trim());
		        	}
		        }
	    	}
	    }
	    String[] result = new String[emptyNames.size()];
	    return emptyNames.toArray(result);
	}

}
