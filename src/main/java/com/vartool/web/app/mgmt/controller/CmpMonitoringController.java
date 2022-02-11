package com.vartool.web.app.mgmt.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.vartech.common.app.beans.ResponseResult;
import com.vartool.web.app.mgmt.service.CmpMonitoringService;
import com.vartool.web.module.HttpUtil;

@Controller
@RequestMapping("/mgmt/cmpMonitoring")
public class CmpMonitoringController {
	
	/** The Constant logger. */
	private final static Logger logger = LoggerFactory.getLogger(CmpMonitoringController.class);
	
	@Autowired
	private CmpMonitoringService cmpMonitoringService; 
	
	@RequestMapping({ "", "/" })
	public ModelAndView main(HttpServletRequest req, HttpServletResponse res, ModelAndView mav) throws Exception {
		
		ModelMap model = mav.getModelMap();
		model.addAttribute("originalURL", HttpUtil.getOriginatingRequestUri(req));
		return new ModelAndView("/mgmt/cmpMonitoring", model);
	}
	
	/**
	 * 
	 * @Method Name  : logInfolist
	 * @Method 설명 : 로그 정보 목록
	 * @작성자   : ytkim
	 * @작성일   : 2020. 2. 5. 
	 * @변경이력  :
	 * @param req
	 * @param res
	 * @param mav
	 * @return
	 * @throws Exception
	 */
	@PostMapping({"/list" })
	public @ResponseBody ResponseResult logInfolist(HttpServletRequest req,	HttpServletResponse res, ModelAndView mav) throws Exception {
		return cmpMonitoringService.logInfoList(HttpUtil.getAllParameter(req));
	}
	
	@PostMapping({"/checkComponent" })
	public @ResponseBody ResponseResult checkComponent(@RequestParam(value = "cmpInfo", required = true) String cmpInfo, HttpServletRequest req,	HttpServletResponse res, ModelAndView mav) throws Exception {
		return cmpMonitoringService.checkComponent(cmpInfo,HttpUtil.getAllParameter(req));
	}
	
	/**
	 * 
	 * @Method Name  : clearLog
	 * @Method 설명 : 로그 정보 삭제. 
	 * @작성자   : ytkim
	 * @작성일   : 2020. 2. 5.
	 * @변경이력  :
	 * @param req
	 * @param res
	 * @param mav
	 * @return
	 * @throws Exception
	 */
	@PostMapping({"/clearLog" })
	public @ResponseBody ResponseResult clearLog(@RequestParam(value = "cmpId", required = true) String cmpId 
			, @RequestParam(value = "type", required = true) String type 
			, HttpServletRequest req) throws Exception {
		return cmpMonitoringService.clearLog(cmpId, type);
	}
	
	@PostMapping({"/startTail" })
	public @ResponseBody ResponseResult startTail(@RequestParam(value = "cmpId", required = true) String cmpId, 
			HttpServletRequest req) throws Exception {
		
		return cmpMonitoringService.startTail(cmpId, HttpUtil.getAllParameter(req));
	}
	
	@PostMapping({"/stopTail" })
	public @ResponseBody ResponseResult stopTail(@RequestParam(value = "cmpId", required = true) String cmpId, 
			HttpServletRequest req) throws Exception {
		
		return cmpMonitoringService.stopTail(cmpId);
	}
	
	@PostMapping({"/killProcess" })
	public @ResponseBody ResponseResult killProcess(@RequestParam(value = "cmpId", required = true) String cmpId, 
			HttpServletRequest req) throws Exception {
		
		return cmpMonitoringService.killProcess(cmpId);
	}
}
