package com.vartool.web.app.common.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.vartool.web.constants.VIEW_PAGE;



/**
 * error controller
* 
* @fileName	: ErrorController.java
* @author	: ytkim
 */
@Controller
@RequestMapping("/error")
public class ErrorController extends AbstractController {

	/** The Constant logger. */
	private final Logger logger = LoggerFactory.getLogger(ErrorController.class);

	@RequestMapping(value = {"", "/"}, method ={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView errorMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return getModelAndView("/error/defaultErrorPage", VIEW_PAGE.COMMONPAGE);
	}

	@RequestMapping(value = "/error403", method ={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView error403(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return getModelAndView("/error/error403", VIEW_PAGE.COMMONPAGE);
	}

	@RequestMapping(value = "/page403csrf", method ={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView page403csrf(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return getModelAndView("/error/page403csrf", VIEW_PAGE.COMMONPAGE);
	}

	@RequestMapping(value = "/error404", method ={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView error404(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return getModelAndView("/error/error404", VIEW_PAGE.COMMONPAGE);
	}

	@RequestMapping(value = "/error500", method ={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView error500(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return getModelAndView("/error/error500", VIEW_PAGE.COMMONPAGE);
	}
	/**
	 *
	 * @Method Name  : connError
	 * @Method ?????? : db ????????? ??????
	 * @?????????   : ytkim
	 * @?????????   : 2019. 11. 1.
	 * @????????????  :
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/connError", method ={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView connError(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return getModelAndView("/error/connError", VIEW_PAGE.COMMONPAGE);
	}

	/**
	 *
	 * @Method Name  : connCreateError
	 * @Method ?????? : db ????????? ?????? ??????
	 * @?????????   : ytkim
	 * @?????????   : 2019. 11. 1.
	 * @????????????  :
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/connCreateError", method ={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView connCreateError(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return getModelAndView("/error/connCreateError", VIEW_PAGE.COMMONPAGE);
	}

	/**
	 *
	 * @Method Name  : blockUser
	 * @Method ?????? : ????????? ?????????
	 * @?????????   : ytkim
	 * @?????????   : 2019. 11. 1.
	 * @????????????  :
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/blockUser", method ={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView blockUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return getModelAndView("/error/blockUser", VIEW_PAGE.COMMONPAGE);
	}
	
	@RequestMapping(value = "/invalidToken", method ={RequestMethod.GET})
	public ModelAndView invalidToken(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return getModelAndView("/error/invalidToken", VIEW_PAGE.COMMONPAGE);
	}
	
	/**
	 *
	 * @Method Name  : dataDownloadError
	 * @Method ?????? : data download exception
	 * @?????????   : ytkim
	 * @?????????   : 2019. 11. 1.
	 * @????????????  :
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/dataDownloadError", method ={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView dataDownloadError(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return getModelAndView("/error/dataDownloadError", VIEW_PAGE.COMMONPAGE);
	}
}
