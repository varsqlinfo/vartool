package com.vartool.web.app.mgmt.controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.utils.HttpUtils;
import com.vartool.web.app.mgmt.service.CredentialsProviderMgmtService;
import com.vartool.web.constants.CredentialsType;
import com.vartool.web.dto.request.CredentialsProviderRequestDTO;
import com.vartool.web.module.VartoolUtils;

/**
 * Credentials Provider management
* 
* @fileName	: CmpLogMgmtController.java
* @author	: ytkim
 */
@Controller
@RequestMapping("/mgmt/cred")
public class CredentialsProviderMgmtController {
	

	/** The Constant logger. */
	private final static Logger logger = LoggerFactory.getLogger(CredentialsProviderMgmtController.class);
	
	@Autowired
	private CredentialsProviderMgmtService credentialsProviderMgmtService;
	
	@GetMapping({"","/"})
	public ModelAndView main(HttpServletRequest req, HttpServletResponse res, ModelAndView mav) throws Exception {
		
		ModelMap model = mav.getModelMap();
		model.addAttribute("originalURL", VartoolUtils.getOriginatingRequestUri(req));
		model.addAttribute("credentialsTypeList", CredentialsType.values());
		return new ModelAndView("/mgmt/credentialsProviderMgmt", model);
	}
	
	@PostMapping({"/list" })
	public @ResponseBody ResponseResult deployList(HttpServletRequest req,
			HttpServletResponse res, ModelAndView mav) throws Exception {
		
		return credentialsProviderMgmtService.list(HttpUtils.getSearchParameter(req));
	}
	
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
	public @ResponseBody ResponseResult save(@Valid CredentialsProviderRequestDTO dto, BindingResult result, ModelAndView mav, HttpServletRequest req) throws Exception {
		
		ResponseResult resultObject = new ResponseResult();
		if (result.hasErrors()) {
			for (ObjectError errorVal : result.getAllErrors()) {
				logger.warn("###  changeLogItemInfo validation check {}", errorVal.toString());
			}
			return VartoolUtils.getResponseResultValidItem(resultObject, result);
		}
		    
		return credentialsProviderMgmtService.save(dto);
	}
	
	/**
	 * 삭제
	 *
	 * @method : remove
	 * @param cmpId
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@PostMapping({"/remove" })
	public @ResponseBody ResponseResult remove(@RequestParam(value = "credId", required = true) String credId, HttpServletRequest req) throws Exception {
		return credentialsProviderMgmtService.remove(credId);
	}
	
	/**
	 * 복사
	 *
	 * @method : copy
	 * @param cmpId
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@PostMapping({"/copy" })
	public @ResponseBody ResponseResult copy(@RequestParam(value = "credId", required = true) String credId, HttpServletRequest req) throws Exception {
		return credentialsProviderMgmtService.copyInfo(credId);
	}
}
