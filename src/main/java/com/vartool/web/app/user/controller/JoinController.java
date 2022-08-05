package com.vartool.web.app.user.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.constants.RequestResultCode;
import com.vartech.common.crypto.EncryptDecryptException;
import com.vartool.web.app.common.controller.AbstractController;
import com.vartool.web.app.user.service.JoinServiceImpl;
import com.vartool.web.constants.VIEW_PAGE;
import com.vartool.web.dto.request.UserReqeustDTO;
import com.vartool.web.module.VartoolUtils;


/**
 * 회원가입
* 
* @fileName	: JoinController.java
* @author	: ytkim
 */
@Controller
@RequestMapping("/join")
public class JoinController extends AbstractController {

	private final Logger logger = LoggerFactory.getLogger(JoinController.class);

	@Autowired
	private JoinServiceImpl joinServiceImpl;

	@RequestMapping(value="/",method=RequestMethod.GET)
	public ModelAndView joinForm(HttpServletRequest request, HttpServletResponse response) {
		return getModelAndView("/joinForm", VIEW_PAGE.JOIN);
	}

	@RequestMapping(value="/save",method=RequestMethod.POST)
	public @ResponseBody ResponseResult insertUserInfo(@Valid UserReqeustDTO joinForm, BindingResult result, ModelAndView mav, HttpServletRequest req) throws EncryptDecryptException {
		
		if(result.hasErrors()){

			for(ObjectError errorVal :result.getAllErrors()){
				logger.warn("###  insertUserInfo validation check {}",errorVal.toString());
			}
			return VartoolUtils.getResponseResultValidItem(result);
		}
		
		ResponseResult resultObject = new ResponseResult();
		Long idCheck = joinServiceImpl.idCheck(joinForm.getUid()).getItem();

		if(idCheck > 0){
			resultObject.setResultCode(RequestResultCode.DUPLICATES);
		}

		resultObject.setItemOne(joinServiceImpl.saveUser(joinForm));

		return resultObject;
	}

	@RequestMapping(value = "/idCheck", method = RequestMethod.POST)
	public @ResponseBody ResponseResult idCheck(@RequestParam(value = "uid" , required = true)  String uid) {
		return joinServiceImpl.idCheck(uid);
	}

	@RequestMapping(value = "/emailCheck", method = RequestMethod.POST)
	public @ResponseBody ResponseResult emailCheck(@RequestParam(value = "uemail" , required = true)  String uemail) {
		return joinServiceImpl.emailCheck(uemail);
	}
}
