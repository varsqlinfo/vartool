package com.vartool.web.app.user.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.utils.HttpUtils;
import com.vartool.web.app.common.controller.AbstractController;
import com.vartool.web.app.user.service.UserPreferencesServiceImpl;
import com.vartool.web.constants.LocaleConstants;
import com.vartool.web.constants.VIEW_PAGE;
import com.vartool.web.dto.request.PasswordRequestDTO;
import com.vartool.web.dto.request.UserModReqeustDTO;
import com.vartool.web.module.SecurityUtil;
import com.vartool.web.module.VartoolUtils;

/**
 *
 *
*-----------------------------------------------------------------------------
* @PROJECT	: varsql
* @NAME		: PreferencesController.java
* @DESC		: 사용자 환경설정 컨트롤러.
* @AUTHOR	: ytkim
*-----------------------------------------------------------------------------
  DATE			AUTHOR			DESCRIPTION
*-----------------------------------------------------------------------------
*2017. 11. 28. 			ytkim			최초작성

*-----------------------------------------------------------------------------
 */
@Controller
@RequestMapping("/user/preferences")
public class UserPreferencesController extends AbstractController{

	private final Logger logger = LoggerFactory.getLogger(UserPreferencesController.class);

	@Autowired
	private UserPreferencesServiceImpl userPreferencesServiceImpl;

	/**
	 *
	 * @Method Name  : preferencesMain
	 * @Method 설명 : 사용자 정보 환결 설정.
	 * @작성자   : ytkim
	 * @작성일   : 2017. 11. 29.
	 * @변경이력  :
	 * @param req
	 * @param res
	 * @param mav
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"","/","/main"}, method =RequestMethod.GET)
	public ModelAndView preferencesMain(HttpServletRequest req, HttpServletResponse res,ModelAndView mav) throws Exception {
		ModelMap model = mav.getModelMap();

		setModelDefaultValue(req , model);
		model.addAttribute("detailInfo" , userPreferencesServiceImpl.findUserInfo(SecurityUtil.userViewId(req)));
		model.addAttribute("localeInfo" , LocaleConstants.values());
		return getModelAndView("/general", VIEW_PAGE.USER_PREFERENCES, model);
	}

	private void setModelDefaultValue(HttpServletRequest req, ModelMap model) {
		model.addAttribute("headerview", ("N".equals(req.getParameter("header"))?"N":""));
		model.addAttribute("originalURL", HttpUtils.getOriginatingRequestUri(req));
	}

	/**
	 *
	 * @Method Name  : userInfoSave
	 * @Method 설명 : 사용자 정보 업데이트.
	 * @작성자   : ytkim
	 * @작성일   : 2017. 11. 29.
	 * @변경이력  :
	 * @param userForm
	 * @param result
	 * @param res
	 * @param mav
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/userInfoSave"}, method = RequestMethod.POST)
	public @ResponseBody ResponseResult userInfoSave(@Valid UserModReqeustDTO userForm, BindingResult result,HttpServletRequest req, HttpServletResponse res) throws Exception {
		if(result.hasErrors()){
			for(ObjectError errorVal :result.getAllErrors()){
				logger.warn("###  UserMainController userInfoSave check {}",errorVal.toString());
			}
			return VartoolUtils.getResponseResultValidItem(result);
		}
		return VartoolUtils.getResponseResultItemOne(userPreferencesServiceImpl.updateUserInfo(userForm,req,res));
	}

	/**
	 *
	 * @Method Name  : preferencesPassword
	 * @Method 설명 : 패스워드 변경.
	 * @작성자   : ytkim
	 * @작성일   : 2017. 11. 29.
	 * @변경이력  :
	 * @param req
	 * @param res
	 * @param mav
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/password"}, method =RequestMethod.GET)
	public ModelAndView preferencesPassword(HttpServletRequest req, HttpServletResponse res,ModelAndView mav) throws Exception {
		ModelMap model = mav.getModelMap();
		setModelDefaultValue(req , model);

		return getModelAndView("/password", VIEW_PAGE.USER_PREFERENCES, model);
	}

	/**
	 *
	 * @Method Name  : passwordSave
	 * @Method 설명 : 비밀번호 변경.
	 * @작성자   : ytkim
	 * @작성일   : 2017. 11. 29.
	 * @변경이력  :
	 * @param passwordForm
	 * @param result
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/passwordSave", method = RequestMethod.POST)
	public @ResponseBody ResponseResult passwordSave(@Valid PasswordRequestDTO passwordForm, BindingResult result,HttpServletRequest req) throws Exception {
		if(result.hasErrors()){
			for(ObjectError errorVal :result.getAllErrors()){
				logger.warn("###  UserMainController passwordSave check {}",errorVal.toString());
			}
			return VartoolUtils.getResponseResultValidItem(result);
		}
		
		passwordForm.setViewid(SecurityUtil.userViewId(req));
		return userPreferencesServiceImpl.updatePasswordInfo(passwordForm);
	}
}
