package com.vartool.web.app.user.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.vartool.web.app.common.controller.AbstractController;
import com.vartool.web.constants.VIEW_PAGE;

/**
 * guest controller
* 
* @fileName	: GuestController.java
* @author	: ytkim
 */
@Controller
@RequestMapping("/guest")
public class GuestController extends AbstractController  {

	@RequestMapping(value = {"","/","/main"}, method =RequestMethod.GET)
	public ModelAndView mainpage(HttpServletRequest req, HttpServletResponse res,ModelAndView mav) throws Exception {
		return getModelAndView("/guestMain" , VIEW_PAGE.GUEST);
	}
}
