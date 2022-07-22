package com.vartool.web.app.mgmt.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.utils.HttpUtils;
import com.vartool.web.app.mgmt.service.UserInfoMgmtService;
import com.vartool.web.module.HttpUtil;

@Controller
@RequestMapping("/mgmt/userInfoMgmt")
public class UserInfoMgmtController {
	

	/** The Constant logger. */
	private final static Logger logger = LoggerFactory.getLogger(UserInfoMgmtController.class);
	
	@Autowired
	private UserInfoMgmtService userInfoMgmtService;
	
	@GetMapping({"","/"})
	public ModelAndView main(HttpServletRequest req, HttpServletResponse res, ModelAndView mav) throws Exception {
		
		ModelMap model = mav.getModelMap();
		model.addAttribute("originalURL", HttpUtil.getOriginatingRequestUri(req));
		return new ModelAndView("/mgmt/userInfoMgmt", model);
	}
	
	@PostMapping({"/list" })
	public @ResponseBody ResponseResult list(HttpServletRequest req,
			HttpServletResponse res, ModelAndView mav) throws Exception {
		
		return userInfoMgmtService.list(HttpUtils.getSearchParameter(req));
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
	public @ResponseBody ResponseResult remove(@RequestParam(value = "viewid", required = true) String viewid, HttpServletRequest req) throws Exception {
		return userInfoMgmtService.remove(viewid);
	}
	
	@PostMapping({"/mappingInfo" })
	public @ResponseBody ResponseResult mappingInfo(@RequestParam(value = "viewid", required = true) String viewid) throws Exception {
		return userInfoMgmtService.mappingInfo(viewid);
	}
	
	@PostMapping({"/modifyMappingInfo" })
	public @ResponseBody ResponseResult modifyUserMappingInfo(@RequestParam(value = "viewid", required = true) String viewid
			, @RequestParam(value = "groupId", required = true) String groupId
			, @RequestParam(value = "mode", required = true, defaultValue = "del") String mode
			, HttpServletRequest req) throws Exception {
		return userInfoMgmtService.modifyUserMappingInfo(viewid, groupId, mode);
	}
	
	/**
	 *
	 * @Method Name  : updAcceptYn
	 * @Method 설명 : 접근 권한 셋팅
	 * @작성자   : ytkim
	 * @작성일   : 2018. 12. 7.
	 * @변경이력  :
	 * @param acceptyn
	 * @param selectItem
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/acceptYn", method=RequestMethod.POST)
	public @ResponseBody ResponseResult updAcceptYn(@RequestParam(value = "acceptyn", required = true )  String acceptyn
			,@RequestParam(value = "selectItem", required = true )  String selectItem
			) throws Exception {

		return userInfoMgmtService.updateAccept(acceptyn ,selectItem);
	}
	
	/**
	 * @method  : initPassword
	 * @desc : 패스워드 초기화
	 * @author   : ytkim
	 * @date   : 2020. 4. 30.
	 * @param viewid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/initPassword", method=RequestMethod.POST)
	public @ResponseBody ResponseResult initPassword(@RequestParam(value = "viewid", required = true )  String viewid) throws Exception {
		return userInfoMgmtService.initPassword(viewid);
	}
}
