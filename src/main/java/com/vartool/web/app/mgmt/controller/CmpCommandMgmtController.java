package com.vartool.web.app.mgmt.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.vartool.web.app.mgmt.service.CmpCommnadMgmtService;
import com.vartool.web.app.mgmt.service.CredentialsProviderMgmtService;
import com.vartool.web.dto.request.CmpCommandRequestDTO;
import com.vartool.web.module.VartoolUtils;

import lombok.RequiredArgsConstructor;

/**
 * command management 
* 
* @fileName	: CmpCommandMgmtController.java
* @author	: ytkim
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/mgmt/cmp/commandMgmt")
public class CmpCommandMgmtController {
	

	/** The Constant logger. */
	private final static Logger logger = LoggerFactory.getLogger(CmpCommandMgmtController.class);
	
	final private CmpCommnadMgmtService cmpCommnadMgmtService;
	
	final private CredentialsProviderMgmtService credentialsProviderMgmtService;
	
	@GetMapping({"","/"})
	public ModelAndView main(HttpServletRequest req, HttpServletResponse res, ModelAndView mav) throws Exception {
		
		ModelMap model = mav.getModelMap();
		model.addAttribute("originalURL", VartoolUtils.getOriginatingRequestUri(req));
		model.addAttribute("allCredList", credentialsProviderMgmtService.allCredentials());
		return new ModelAndView("/mgmt/cmpCommandMgmt", model);
	}
	
	/**
	 * command 목록 
	 *
	 * @method : list
	 * @param req
	 * @param res
	 * @param mav
	 * @return
	 * @throws Exception
	 */
	@PostMapping({"/list" })
	public @ResponseBody ResponseResult list(HttpServletRequest req,
			HttpServletResponse res, ModelAndView mav) throws Exception {
		
		return cmpCommnadMgmtService.list(HttpUtils.getSearchParameter(req));
	}
	
	/**
	 * 배포 정보 저장.
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
	public @ResponseBody ResponseResult save(@Valid CmpCommandRequestDTO dto, BindingResult result, ModelAndView mav, HttpServletRequest req) throws Exception {
		
		ResponseResult resultObject = new ResponseResult();
		if (result.hasErrors()) {
			for (ObjectError errorVal : result.getAllErrors()) {
				logger.warn("###  changeLogItemInfo validation check {}", errorVal.toString());
			}
			return VartoolUtils.getResponseResultValidItem(resultObject, result);
		}
		    
		return cmpCommnadMgmtService.save(dto);
	}
	
	/**
	 * 정보 삭제.  
	 *
	 * @method : remove
	 * @param cmpId
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@PostMapping({"/remove" })
	public @ResponseBody ResponseResult remove(@RequestParam(value = "cmpId", required = true) String cmpId, HttpServletRequest req) throws Exception {
		return cmpCommnadMgmtService.remove(cmpId);
	}
	
	/**
	 * copy
	 *
	 * @method : copy
	 * @param cmpId
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@PostMapping({"/copy" })
	public @ResponseBody ResponseResult copy(@RequestParam(value = "cmpId", required = true) String cmpId, HttpServletRequest req) throws Exception {
		return cmpCommnadMgmtService.copyInfo(cmpId);
	}
}
