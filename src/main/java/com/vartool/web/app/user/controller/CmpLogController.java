package com.vartool.web.app.user.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vartech.common.app.beans.ResponseResult;
import com.vartool.web.app.user.service.CmpLogService;

/**
 * 로그 
* 
* @fileName	: CmpLogController.java
* @author	: ytkim
 */
@Controller
@RequestMapping("/cmp/log")
public class CmpLogController {
	

	/** The Constant logger. */
	private final static Logger logger = LoggerFactory.getLogger(CmpLogController.class);
	
	@Autowired
	private CmpLogService cmpLogService;
	
	/**
	 * 로그 정보 로드
	 *
	 * @method : loadAppLog
	 * @param cmpId
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@PostMapping({"/load" })
	@ResponseBody
	public ResponseResult loadAppLog(@RequestParam(value = "cmpId", required = true) String cmpId, HttpServletRequest req) throws Exception {
		return cmpLogService.loadLog(cmpId, true);
	}
}
