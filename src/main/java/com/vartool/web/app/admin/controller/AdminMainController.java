package com.vartool.web.app.admin.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.vartech.common.utils.VartechUtils;
import com.vartool.web.app.common.controller.AbstractController;
import com.vartool.web.app.config.VartoolConfiguration;
import com.vartool.web.constants.VIEW_PAGE;

@Controller
@RequestMapping("/admin")
public class AdminMainController extends AbstractController {
	

	/** The Constant logger. */
	private final static Logger logger = LoggerFactory.getLogger(AdminMainController.class);
	
	@RequestMapping({"", "/"})
	public ModelAndView mainPage(HttpServletRequest req, HttpServletResponse res, ModelAndView mav) throws Exception {
		return getRedirectModelAndView(VIEW_PAGE.ADMIN.getViewPage("/managerMgmt"));
	}
	
	@RequestMapping({ "/vartoolConfig" })
	public ModelAndView vartoolInfo(HttpServletRequest req,	HttpServletResponse res, ModelAndView mav) throws Exception {
		
		ModelMap model = mav.getModelMap();
		model.addAttribute("configInfo", VartechUtils.objectToJsonString(VartoolConfiguration.getInstance().getConfigInfo()));
		
		return getModelAndView("/vartoolConfig", VIEW_PAGE.ADMIN, model);
	}
	
}
