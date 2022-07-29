package com.vartool.web.security;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import com.vartech.common.utils.HttpUtils;
import com.vartool.web.constants.LocaleConstants;
import com.vartool.web.module.CommonUtils;
import com.vartool.web.module.SecurityUtil;
import com.vartool.web.module.VartoolUtils;
import com.vartool.web.security.auth.AuthorityType;

@Component
public class VartoolAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	private final Logger logger = LoggerFactory.getLogger(VartoolAuthenticationSuccessHandler.class);

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	private RequestCache requestCache = new HttpSessionRequestCache();

	public VartoolAuthenticationSuccessHandler() {
		super();
		super.setUseReferer(true);
	}

	public void onAuthenticationSuccess(final HttpServletRequest request,
			final HttpServletResponse response,
			final Authentication authentication) throws IOException, ServletException {

		User userInfo = SecurityUtil.loginInfo();
		
		userInfo.setUserIp(CommonUtils.getClientIp(request));
		String targetUrl = userRedirectTargetUrl(request ,response, userInfo, authentication);

		if (response.isCommitted()) {
			logger.debug("Response has already been committed. Unable to redirect to {} ", targetUrl);
			return;
		}

		if(userInfo.isLoginRememberMe()) {
			super.clearAuthenticationAttributes(request);

			String queryStr = request.getQueryString();
			//String reqUrl = request.getRequestURI().replaceFirst(request.getContextPath(), "") +(StringUtil.isBlank(queryStr)?"":"?"+queryStr);
			String reqUrl = request.getRequestURI().replaceFirst(request.getContextPath(), "");
			
			logger.debug("remember me forward request uri : {}, query string :{}" , reqUrl , queryStr);
			logger.debug("cookie values : {} " , HttpUtils.getAllCookieString(request));
			logger.debug("request header : {} " , HttpUtils.getAllReqHeaderString(request));
			logger.debug("response header : {} " , HttpUtils.getAllResHeaderString(response));
			logger.debug("----------------------------------------------------------------------");
			request.getRequestDispatcher(reqUrl).forward(request, response);
			
		    return ;
		}else {

			if(!VartoolUtils.isAjaxRequest(request)) {
				SavedRequest savedRequest = requestCache.getRequest(request, response);

			    if(savedRequest != null) {

			    	String contextPath =request.getContextPath();

			    	int contextPosIdx = targetUrl.indexOf(contextPath);

			    	if(contextPosIdx > -1) {
			    		String url = targetUrl.substring(contextPosIdx + contextPath.length());
				    	if(!"".equals(url) && !"/".equals(url)) {
				    		targetUrl = savedRequest.getRedirectUrl();
				    	}
			    	}
			    }
			}
		    logger.debug("login targer url : {}", targetUrl);
			redirectStrategy.sendRedirect(request, response, targetUrl);
			super.clearAuthenticationAttributes(request);
		}
	}
	
	private String userRedirectTargetUrl(HttpServletRequest request, HttpServletResponse response,User userInfo , final Authentication authentication) {

		AuthorityType topAuthority = userInfo.getTopAuthority();

		List<AuthorityType> userScreen = new ArrayList<AuthorityType>();
		for(AuthorityType auth : AuthorityType.values()){
			if(!AuthorityType.GUEST.equals(auth) &&  topAuthority.getPriority() >=auth.getPriority()){
				userScreen.add(auth);
			}
		}
		String lang = request.getParameter("lang");

		if(lang !=null && !"".equals(lang)){
			Locale userLacle= LocaleConstants.parseLocaleString(lang);
			if( userLacle != null) {
				userInfo.setUserLocale(userLacle);
			}
		}

		request.getSession().setAttribute("var.user.screen", userScreen);

		return userInfo.getTopAuthority().mainPage();
	}

}