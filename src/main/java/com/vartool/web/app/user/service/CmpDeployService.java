package com.vartool.web.app.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vartech.common.app.beans.ParamMap;
import com.vartech.common.app.beans.ResponseResult;
import com.vartool.web.app.handler.deploy.DeployCmpManager;
import com.vartool.web.app.handler.deploy.DeployInterface;
import com.vartool.web.app.handler.deploy.git.GitDeployComponent;
import com.vartool.web.app.handler.deploy.svn.SvnDeployComponent;
import com.vartool.web.constants.AppCode;
import com.vartool.web.dto.response.CmpDeployResponseDTO;
import com.vartool.web.dto.websocket.LogMessageDTO;
import com.vartool.web.model.entity.cmp.CmpItemDeployEntity;
import com.vartool.web.repository.cmp.CmpItemDeployRepository;

@Service
public class CmpDeployService{
	private final static Logger logger = LoggerFactory.getLogger(CmpDeployService.class);
	
	@Autowired
	private GitDeployComponent gitDeployComponent;
	
	@Autowired
	private SvnDeployComponent svnDeployComponent;
	
	@Autowired
	private CmpItemDeployRepository cmpItemDeployRepository;
	
	/**
	 * 
	 * @Method Name  : loadLog
	 * @Method 설명 : get deploy log 
	 * @작성자   : ytkim
	 * @작성일   : 2021. 8. 13.
	 * @변경이력  :
	 * @param cmpId
	 * @param allParameter
	 * @return
	 */
	public ResponseResult loadLog(String cmpId, ParamMap allParameter) {
		ResponseResult responseResult = new ResponseResult();
		responseResult.setItemOne(LogMessageDTO.builder().cmpId(cmpId).log(DeployCmpManager.getInstance().getLogContent(cmpId)).build());
		return responseResult;
	}
	
	/**
	 * 
	 * @Method Name  : deploy
	 * @Method 설명 : 
	 * @작성자   : ytkim
	 * @작성일   : 2020. 4. 20.
	 * @변경이력  :
	 * @param deployReqDto
	 * @return
	 */
	public ResponseResult deploy(String cmpId, String action) {
		CmpItemDeployEntity entity = cmpItemDeployRepository.findByCmpId(cmpId);
		
		ResponseResult result = new ResponseResult();
		if(entity==null){
			result.setMessage("deploy item not found : "+ cmpId);
			return result;
		}
		
		CmpDeployResponseDTO dto = CmpDeployResponseDTO.toDto(entity);
		
		if(DeployCmpManager.getInstance().isRunning(dto.getCmpId())) {
			result.setMessage("already running");
			return result;
		}
		
		dto.setAction(action);
		
		return getDeployComponent(dto).deployAction(dto);
	}
	
	private DeployInterface getDeployComponent(CmpDeployResponseDTO dto) {
		if(AppCode.SCM_TYPE.GIT.isEquals(dto.getScmType())) {
			return gitDeployComponent;
		}else if(AppCode.SCM_TYPE.SVN.isEquals(dto.getScmType())) {
			return svnDeployComponent;
		}
		
		return null; 
	}
}
