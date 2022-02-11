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
import com.vartool.web.app.mgmt.service.CmpLogMgmtService;
import com.vartool.web.dto.request.CmpLogRequestDTO;
import com.vartool.web.module.HttpUtil;
import com.vartool.web.module.VartoolUtils;

@Controller
@RequestMapping("/mgmt/cmp/logMgmt")
public class CmpLogMgmtController {
	

	/** The Constant logger. */
	private final static Logger logger = LoggerFactory.getLogger(CmpLogMgmtController.class);
	
	@Autowired
	private CmpLogMgmtService cmpLogMgmtService;
	
	
	@GetMapping({"","/"})
	public ModelAndView main(HttpServletRequest req, HttpServletResponse res, ModelAndView mav) throws Exception {
		
		ModelMap model = mav.getModelMap();
		model.addAttribute("originalURL", HttpUtil.getOriginatingRequestUri(req));
		return new ModelAndView("/mgmt/cmpLogMgmt", model);
	}
	
	@PostMapping({"/list" })
	public @ResponseBody ResponseResult deployList(HttpServletRequest req,
			HttpServletResponse res, ModelAndView mav) throws Exception {
		
		return cmpLogMgmtService.list(HttpUtils.getSearchParameter(req));
	}
	
	/**
	 * 
	 * @Method Name  : changeDeployInfo
	 * @Method 설명 : 배포 정보 저장.
	 * @작성자   : ytkim
	 * @작성일   : 2020. 2. 5. 
	 * @변경이력  :
	 * @param req
	 * @param res
	 * @param mav
	 * @return
	 * @throws Exception
	 */
	@PostMapping({"/save" })
	public @ResponseBody ResponseResult save(@Valid CmpLogRequestDTO dto, BindingResult result, ModelAndView mav, HttpServletRequest req) throws Exception {
		
		ResponseResult resultObject = new ResponseResult();
		if (result.hasErrors()) {
			for (ObjectError errorVal : result.getAllErrors()) {
				logger.warn("###  changeLogItemInfo validation check {}", errorVal.toString());
			}
			return VartoolUtils.getResponseResultValidItem(resultObject, result);
		}
		    
		return cmpLogMgmtService.save(dto);
	}
	/**
	 * 
	 * @Method Name  : remove
	 * @Method 설명 : deploy 정보 삭제.  
	 * @작성자   : ytkim
	 * @작성일   : 2020. 2. 5.
	 * @변경이력  :
	 * @param uid
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@PostMapping({"/remove" })
	public @ResponseBody ResponseResult remove(@RequestParam(value = "cmpId", required = true) String cmpId, HttpServletRequest req) throws Exception {
		return cmpLogMgmtService.remove(cmpId);
	}
	
	@PostMapping({"/copy" })
	public @ResponseBody ResponseResult copy(@RequestParam(value = "cmpId", required = true) String cmpId, HttpServletRequest req) throws Exception {
		return cmpLogMgmtService.copyInfo(cmpId);
	}
}