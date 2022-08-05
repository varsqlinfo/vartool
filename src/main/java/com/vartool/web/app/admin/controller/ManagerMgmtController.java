package com.vartool.web.app.admin.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.app.beans.SearchParameter;
import com.vartech.common.utils.HttpUtils;
import com.vartool.web.app.admin.service.ManagerMgmtService;
import com.vartool.web.app.common.controller.AbstractController;
import com.vartool.web.constants.VIEW_PAGE;
import com.vartool.web.security.auth.AuthorityType;

/**
 * Manager Management
* 
* @fileName	: ManagerMgmtController.java
* @author	: ytkim
 */
@Controller
@RequestMapping("/admin/managerMgmt")
public class ManagerMgmtController extends AbstractController {

	/** The Constant logger. */
	private final static Logger logger = LoggerFactory.getLogger(ManagerMgmtController.class);
	
	@Autowired
	private ManagerMgmtService managerMgmtService; 
	
	@RequestMapping({"", "/"})
	public ModelAndView mainPage(HttpServletRequest req, HttpServletResponse res, ModelAndView mav) throws Exception {
		return getModelAndView("/managerMgmt", VIEW_PAGE.ADMIN);
	}

	@RequestMapping(value = { "/userList" }, method = { RequestMethod.POST })
	@ResponseBody
	public ResponseResult userList(HttpServletRequest req) throws Exception {
		SearchParameter searchParameter = HttpUtils.getSearchParameter(req);
		return this.managerMgmtService.searchRoleUserList(AuthorityType.USER, searchParameter);
	}

	@RequestMapping(value = { "/managerList" }, method = { RequestMethod.POST })
	@ResponseBody
	public ResponseResult managerlist(HttpServletRequest req) throws Exception {
		SearchParameter searchParameter = HttpUtils.getSearchParameter(req);
		return this.managerMgmtService.searchRoleUserList(AuthorityType.MANAGER, searchParameter);
	}

	@RequestMapping(value = { "/managerRoleMgmt" }, method = { RequestMethod.POST })
	@ResponseBody
	public ResponseResult managerRoleMgmt(
			@RequestParam(value = "mode", required = true, defaultValue = "del") String mode,
			@RequestParam(value = "viewid", required = true) String viewid) throws Exception {
		logger.debug("managerRoleMgmt mode: {} , viewid : {}", mode, viewid);
		return this.managerMgmtService.updateManagerRole(mode, viewid);
	}
	
}
