package com.vartool.web.app.handler.deploy;

import com.vartech.common.app.beans.ResponseResult;
import com.vartool.web.dto.response.CmpDeployResponseDTO;


public interface DeployInterface {

	public ResponseResult autoDeploy(CmpDeployResponseDTO entity);

	public ResponseResult deployAction(CmpDeployResponseDTO dto);
}
