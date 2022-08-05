package com.vartool.web.app.handler.deploy.svn;

import org.springframework.stereotype.Component;

import com.vartech.common.app.beans.ResponseResult;
import com.vartool.web.app.handler.deploy.AbstractDeploy;
import com.vartool.web.app.websocket.service.WebSocketServiceImpl;
import com.vartool.web.dto.DeployInfo;

@Component
public class SvnDeployComponent extends AbstractDeploy{

	public SvnDeployComponent(WebSocketServiceImpl webSocketServiceImpl) {
		super(webSocketServiceImpl);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ResponseResult deployAction(DeployInfo dto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseResult autoDeploy(DeployInfo dto) {
		// TODO Auto-generated method stub
		return null;
	}


}
