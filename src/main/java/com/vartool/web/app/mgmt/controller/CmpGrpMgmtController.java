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
import com.vartool.web.app.mgmt.service.CmpGrpMgmtService;
import com.vartool.web.dto.request.CmpGroupRequestDTO;
import com.vartool.web.module.VartoolUtils;

/**
 * component group management
* 
* @fileName	: CmpGrpMgmtController.java
* @author	: ytkim
 */
@Controller
@RequestMapping("/mgmt/cmpGroupMgmt")
public class CmpGrpMgmtController {
	

	/** The Constant logger. */
	private final static Logger logger = LoggerFactory.getLogger(CmpGrpMgmtController.class);
	
	@Autowired
	private CmpGrpMgmtService cmpGrpMgmtService;
	
	@GetMapping({"","/"})
	public ModelAndView main(HttpServletRequest req, HttpServletResponse res, ModelAndView mav) throws Exception {
		
		ModelMap model = mav.getModelMap();
		model.addAttribute("originalURL", VartoolUtils.getOriginatingRequestUri(req));
		return new ModelAndView("/mgmt/cmpGroupMgmt", model);
	}
	
	@GetMapping({"/user"})
	public ModelAndView cmpGroupNUserMgmt(HttpServletRequest req, HttpServletResponse res, ModelAndView mav) throws Exception {
		
		ModelMap model = mav.getModelMap();
		model.addAttribute("originalURL", VartoolUtils.getOriginatingRequestUri(req));
		return new ModelAndView("/mgmt/cmpGroupNUserMgmt", model);
	}
	
	@PostMapping({"/list" })
	public @ResponseBody ResponseResult list(HttpServletRequest req,
			HttpServletResponse res, ModelAndView mav) throws Exception {
		
		return cmpGrpMgmtService.list(HttpUtils.getSearchParameter(req));
	}
	
	/**
	 * 
	 * @Method Name  : save
	 * @Method 설명 :  정보 저장.
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
	public @ResponseBody ResponseResult save(@Valid CmpGroupRequestDTO dto, BindingResult result, ModelAndView mav, HttpServletRequest req) throws Exception {
		ResponseResult resultObject = new ResponseResult();
		if (result.hasErrors()) {
			for (ObjectError errorVal : result.getAllErrors()) {
				logger.warn("###  component group validation check {}", errorVal.toString());
			}
			return VartoolUtils.getResponseResultValidItem(resultObject, result);
		}
		    
		return cmpGrpMgmtService.save(dto);
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
	public @ResponseBody ResponseResult remove(@RequestParam(value = "groupId", required = true) String groupId, HttpServletRequest req) throws Exception {
		return cmpGrpMgmtService.remove(groupId);
	}
	
	@PostMapping({"/cmpList" })
	public @ResponseBody ResponseResult cmpList(HttpServletRequest req) throws Exception {
		return cmpGrpMgmtService.cmpList(HttpUtils.getSearchParameter(req));
	}
	
	@PostMapping({"/mappingInfo" })
	public @ResponseBody ResponseResult mappingInfo(@RequestParam(value = "groupId", required = true) String groupId) throws Exception {
		return cmpGrpMgmtService.mappingInfo(groupId);
	}
	
	@PostMapping({"/modifyMappingInfo" })
	public @ResponseBody ResponseResult modifyMappingInfo(@RequestParam(value = "selectItem", required = true) String selectItem
			, @RequestParam(value = "groupId", required = true) String groupId
			, @RequestParam(value = "mode", required = true, defaultValue = "del") String mode
			, HttpServletRequest req) throws Exception {
		return cmpGrpMgmtService.modifyMappingInfo(selectItem, groupId, mode);
	}
	
	@PostMapping({"/userList" })
	public @ResponseBody ResponseResult userList(HttpServletRequest req) throws Exception {
		return cmpGrpMgmtService.userList(HttpUtils.getSearchParameter(req));
	}
	
	@PostMapping({"/userMappingInfo" })
	public @ResponseBody ResponseResult userMappingInfo(@RequestParam(value = "groupId", required = true) String groupId) throws Exception {
		return cmpGrpMgmtService.userMappingInfo(groupId);
	}
	
	@PostMapping({"/modifyUserMappingInfo" })
	public @ResponseBody ResponseResult modifyUserMappingInfo(@RequestParam(value = "selectItem", required = true) String selectItem
			, @RequestParam(value = "groupId", required = true) String groupId
			, @RequestParam(value = "mode", required = true, defaultValue = "del") String mode
			, HttpServletRequest req) throws Exception {
		return cmpGrpMgmtService.modifyUserMappingInfo(selectItem, groupId, mode);
	}
}
