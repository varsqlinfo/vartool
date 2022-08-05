package com.vartool.web.app.mgmt.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 매니저 메인
* 
* @fileName	: MgmtMainController.java
* @author	: ytkim
 */
@Controller
@RequestMapping("/mgmt")
public class MgmtMainController {
	

	/** The Constant logger. */
	private final static Logger logger = LoggerFactory.getLogger(MgmtMainController.class);
	
	@RequestMapping({"", "/"})
	public ModelAndView mainPage(HttpServletRequest req, HttpServletResponse res, ModelAndView mav) throws Exception {
		return new ModelAndView("redirect:/mgmt/userInfoMgmt");
	}
}
