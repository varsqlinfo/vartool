package com.vartool.web.app.handler.deploy;

import com.vartech.common.app.beans.ResponseResult;
import com.vartool.web.dto.DeployInfo;


public interface DeployInterface {

	public ResponseResult autoDeploy(DeployInfo deployInfo);

	public ResponseResult deployAction(DeployInfo deployInfo);
}
