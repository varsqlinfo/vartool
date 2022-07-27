package com.vartool.web.app.login;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.vartech.common.app.beans.ResponseResult;
import com.vartool.web.app.common.controller.AbstractController;
import com.vartool.web.app.common.service.UserCommonService;
import com.vartool.web.constants.VIEW_PAGE;
import com.vartool.web.security.User;
import com.vartool.web.security.auth.AuthorityType;
 
@Controller
public class LoginController extends AbstractController{
     
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    
    @Autowired
    private UserCommonService userCommonService; 
     
    /**
     * Simply selects the home view to render by returning its name.
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(HttpSession session) {
        
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        
        if(auth != null && !"anonymousUser".equals(auth.getPrincipal())){
        	final Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

			String url  = AuthorityType.GUEST.mainPage();
			String tmpAuthority = "";
			for (final GrantedAuthority grantedAuthority : authorities) {
				tmpAuthority = grantedAuthority.getAuthority();
				if (tmpAuthority.equals(AuthorityType.USER.name())) {
					url  =  AuthorityType.USER.mainPage();
					break;
				}else if (tmpAuthority.equals(AuthorityType.GUEST.name())) {
					url  =  AuthorityType.GUEST.mainPage();
					break;
				}else if (tmpAuthority.equals(AuthorityType.MANAGER.name())) {
					url  =  AuthorityType.USER.mainPage();
					break;
				}else if (tmpAuthority.equals(AuthorityType.ADMIN.name())) {
					url  =  AuthorityType.USER.mainPage();
					break;
				}
			}

			return getRedirectModelAndView(url);
		}
		
        return getModelAndView("/loginForm", VIEW_PAGE.LOGIN);
        
    }
    
    @RequestMapping(value="/login", params="mode" , method = RequestMethod.POST)
	public ModelAndView loginFailed(HttpServletRequest request, HttpServletResponse response , ModelAndView mav) throws Exception {
		ModelMap model = mav.getModelMap();
		model.addAttribute("login", "fail");

		return  getModelAndView("/loginForm", VIEW_PAGE.LOGIN , model);
	}
     
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ModelAndView logout(HttpSession session) {
        User userDetails = (User)session.getAttribute("userLoginInfo");
         
        logger.info("Welcome logout! {}, {}", session.getId(), userDetails.getUsername());
         
         
        session.invalidate();
        return  new ModelAndView("/login/loginForm");
    }
     
    /**
	 *
	 * @Method Name  : accessDenied
	 * @Method 설명 : 권한없음
	 * @작성자   : ytkim
	 * @작성일   : 2019. 11. 1.
	 * @변경이력  :
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/accessDenied", method = RequestMethod.GET)
	public ModelAndView accessDenied() throws Exception {
		return getRedirectModelAndView("/login");
	}
	
	@RequestMapping(value = "/lostPassword", method = RequestMethod.GET)
	public ModelAndView lostPassword(HttpServletRequest request, HttpServletResponse response , ModelAndView mav) throws Exception {
		return getModelAndView("/lostPassword", VIEW_PAGE.LOGIN);
	}
	
	@RequestMapping(value = "/lostPassword", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult lostPasswordPost(@RequestParam(value = "uid", required = true) String uid, @RequestParam(value = "uemail", required = true) String uemail, HttpServletRequest req) throws Exception {
		return userCommonService.sendPasswordMail(uid, uemail);
	}
	
	/**
	 * @Method Name  : resetPassword
	 * @desc : reset password 
	 * @param request
	 * @param response
	 * @param mav
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/resetPassword", method = RequestMethod.GET)
	public ModelAndView resetPassword(@RequestParam(value = "token", required = true) String token, ModelAndView mav, HttpServletRequest req) throws Exception {
		
		if(userCommonService.isValidToken(token)) {
			ModelMap model = mav.getModelMap();
			return getModelAndView("/resetPassword", VIEW_PAGE.LOGIN, model);
		}else {
			return getModelAndView("/resetPasswordFail", VIEW_PAGE.LOGIN);
		}
	}
	
	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult resetPasswordPost(@RequestParam(value = "token", required = true) String token
			,@RequestParam(value = "upw", required = true) String upw, @RequestParam(value = "confirmUpw", required = true) String confirmUpw,
			HttpServletRequest req) throws Exception {
		
		
		return userCommonService.resetPassword(token, upw, confirmUpw);
		
	}
   
}