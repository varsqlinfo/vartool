package com.vartool.web.app.user.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.vartech.common.app.beans.ResponseResult;
import com.vartool.web.app.user.service.UserMainPrefService;
import com.vartool.web.dto.request.UserPreferencesRequestDTO;
import com.vartool.web.module.VartoolUtils;

/**
 * 사용자 메인  환경설정
* 
* @fileName	: UserPrefController.java
* @author	: ytkim
 */
@Controller
@RequestMapping("/pref")
public class UserMainPrefController {

	/** The Constant logger. */
	private final static Logger logger = LoggerFactory.getLogger(UserMainPrefController.class);
	
	@Autowired
	private UserMainPrefService userPrefService;
	
	/**
	 * 저장
	 *
	 * @method : save
	 * @param dto
	 * @param result
	 * @param mav
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@PostMapping({"/save" })
	public @ResponseBody ResponseResult save(@Valid UserPreferencesRequestDTO dto, BindingResult result, ModelAndView mav, HttpServletRequest req) throws Exception {
			
		ResponseResult resultObject = new ResponseResult();
		if (result.hasErrors()) {
			for (ObjectError errorVal : result.getAllErrors()) {
				logger.warn("###  pref save validation check {}", errorVal.toString());
			}
			return VartoolUtils.getResponseResultValidItem(resultObject, result);
		}
		
		return userPrefService.save(dto);
	}
	
	/**
	 * 기본 레이아웃 적용.
	 *
	 * @method : defaultLayout
	 * @param dto
	 * @param result
	 * @param mav
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@PostMapping({"/defaultLayout" })
	public @ResponseBody ResponseResult defaultLayout(@Valid UserPreferencesRequestDTO dto, BindingResult result, ModelAndView mav, HttpServletRequest req) throws Exception {
			
		ResponseResult resultObject = new ResponseResult();
		if (result.hasErrors()) {
			for (ObjectError errorVal : result.getAllErrors()) {
				logger.warn("###  pref save validation check {}", errorVal.toString());
			}
			return VartoolUtils.getResponseResultValidItem(resultObject, result);
		}
		
		return userPrefService.defaultLayout(dto);
	}
	
	/**
	 * 레이아웃 초기화
	 *
	 * @method : initLayout
	 * @param groupId
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@GetMapping({"/initLayout" })
	public @ResponseBody ResponseResult initLayout(@RequestParam(value = "groupId", required = true) String groupId, HttpServletRequest req) throws Exception {
		
		return userPrefService.initLayout(groupId);
	}
}
