package com.vartool.web.app.user.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vartech.common.app.beans.ResponseResult;
import com.vartool.web.app.user.service.CmpDeployService;
import com.vartool.web.module.HttpUtil;
import com.vartool.web.module.VartoolUtils;

@Controller
@RequestMapping("/cmp/deploy")
public class CmpDeployController {
	
	/** The Constant logger. */
	private final static Logger logger = LoggerFactory.getLogger(CmpDeployController.class);
	
	@Autowired
	private CmpDeployService cmpDeployService;
	
	@PostMapping({"/load" })
	@ResponseBody
	public ResponseResult loadAppLog(@RequestParam(value = "cmpId", required = true) String cmpId, HttpServletRequest req) throws Exception {
		return cmpDeployService.loadLog(cmpId, HttpUtil.getAllParameter(req));
	}
	
	@PreAuthorize("hasAnyAuthority('MANAGER','ADMIN')")
	@PostMapping({"/action" })
	public @ResponseBody ResponseResult deployAction(@RequestParam(value = "cmpId", required = true) String cmpId,
			@RequestParam(value = "action", required = true) String action,
			HttpServletRequest req) throws Exception {
		
		return cmpDeployService.deploy(cmpId, action);
	}
	
}
