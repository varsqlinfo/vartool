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
import com.vartool.web.app.user.service.UserPrefService;
import com.vartool.web.dto.request.UserPreferencesRequestDTO;
import com.vartool.web.module.VartoolUtils;

@Controller
@RequestMapping("/pref")
public class UserPrefController {

	/** The Constant logger. */
	private final static Logger logger = LoggerFactory.getLogger(UserPrefController.class);
	
	@Autowired
	private UserPrefService userPrefService;
	
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
	
	@GetMapping({"/initLayout" })
	public @ResponseBody ResponseResult initLayout(@RequestParam(value = "groupId", required = true) String groupId, HttpServletRequest req) throws Exception {
		
		return userPrefService.initLayout(groupId);
	}
}
