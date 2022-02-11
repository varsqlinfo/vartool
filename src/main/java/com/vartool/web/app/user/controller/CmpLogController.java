package com.vartool.web.app.user.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.vartech.common.app.beans.ResponseResult;
import com.vartool.web.app.user.service.CmpLogService;
import com.vartool.web.module.HttpUtil;

@Controller
@RequestMapping("/cmp/log")
public class CmpLogController {
	

	/** The Constant logger. */
	private final static Logger logger = LoggerFactory.getLogger(CmpLogController.class);
	
	@Autowired
	private CmpLogService cmpLogService;
	
	@PostMapping({"/load" })
	@ResponseBody
	public ResponseResult loadAppLog(@RequestParam(value = "cmpId", required = true) String cmpId, HttpServletRequest req) throws Exception {
		return cmpLogService.loadLog(cmpId, HttpUtil.getAllParameter(req));
	}
}
