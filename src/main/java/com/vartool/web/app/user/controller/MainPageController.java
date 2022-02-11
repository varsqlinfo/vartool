package com.vartool.web.app.user.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.vartech.common.utils.StringUtils;
import com.vartech.common.utils.VartechUtils;
import com.vartool.web.app.user.service.UserCmpService;
import com.vartool.web.dto.response.CmpGroupUserMappingResponseDTO;

@Controller
public class MainPageController {

	/** The Constant logger. */
	private final static Logger logger = LoggerFactory.getLogger(MainPageController.class);
	
	@Autowired
	private UserCmpService userCmpService;

	@RequestMapping({ "", "/", "/main","/main/{groupId}" })
	public ModelAndView mainpage(@PathVariable(value = "groupId") Optional<String> groupId, HttpServletRequest req, ModelAndView mav) throws Exception {
		
		ModelMap model = mav.getModelMap();
		
		CmpGroupUserMappingResponseDTO cgumr= null; 
		
		List<CmpGroupUserMappingResponseDTO> groupList = userCmpService.allUserGroupList(); 
		
		if(groupId.isPresent()) {
			cgumr = groupList.stream().filter(item ->{
				return item.getGroupId().equals(groupId.get());
			}).findAny().orElse(null);
		}else {
			if(groupList.size() > 0) {
				cgumr = groupList.get(0);
				return new ModelAndView("redirect:/main/"+ cgumr.getGroupId(), model);
			}
		}
		
		model.addAttribute("userGroupList", groupList);
		
		if(cgumr == null) {
			model.addAttribute("userCmpMap", "{}");
			return new ModelAndView("/main/mainMessage", model);
		}
		
		String mainSettingInfo = userCmpService.mainLayoutInfo(cgumr); 
		if(!StringUtils.isBlank(mainSettingInfo)) {
			model.addAttribute("mainLayoutInfo", mainSettingInfo);
		}else {
			model.addAttribute("mainLayoutInfo", cgumr.getLayoutInfo());
		}
		
		cgumr.setLayoutInfo(null);
		
		model.addAttribute("cmpGroupInfo", cgumr);
		model.addAttribute("userCmpMap", VartechUtils.objectToJsonString(userCmpService.userCmpInfos(cgumr)));
		
		return new ModelAndView("/main/mainPage", model);
	}
	
}
