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
import com.vartool.web.app.user.service.CmpCommandService;
import com.vartool.web.module.HttpUtil;

@Controller
@RequestMapping("/cmp/command")
public class CmpCommandController {
	

	/** The Constant logger. */
	private final static Logger logger = LoggerFactory.getLogger(CmpCommandController.class);
	
	@Autowired
	private CmpCommandService cmpCommandService;
	
	@PostMapping({"/load" })
	@ResponseBody
	public ResponseResult loadAppLog(@RequestParam(value = "cmpId", required = true) String cmpId, HttpServletRequest req) throws Exception {
		return cmpCommandService.loadLog(cmpId, HttpUtil.getAllParameter(req));
	}
	
	@PreAuthorize("hasAnyAuthority('MANAGER','ADMIN')")
	@PostMapping({"/startAndStop" })
	@ResponseBody
	public ResponseResult startAndStop(@RequestParam(value = "cmpId", required = true) String cmpId,
			@RequestParam(value = "action", required = true) String action, HttpServletRequest req) throws Exception {
		return cmpCommandService.startAndStop(cmpId, action, HttpUtil.getAllParameter(req));
	}
}
