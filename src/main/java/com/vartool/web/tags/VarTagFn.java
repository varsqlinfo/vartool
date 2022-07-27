package com.vartool.web.tags;

import javax.servlet.http.HttpServletRequest;

import com.vartech.common.utils.DateUtils;
import com.vartech.common.utils.VartechUtils;
import com.vartool.web.app.config.VartoolConfiguration;
import com.vartool.web.constants.VartoolConstants;

/**
 * -----------------------------------------------------------------------------
* @fileName		: VarsqlFn.java
* @desc		: varsql custom tag function
* @author	: ytkim
*-----------------------------------------------------------------------------
  DATE			AUTHOR			DESCRIPTION
*-----------------------------------------------------------------------------
*2020. 4. 24. 			ytkim			최초작성

*-----------------------------------------------------------------------------
 */
public final class VarTagFn{

	public static String objectToJson(Object json) {
		return VartechUtils.objectToJsonString(json);
	}

	public static String currentDate(String foramt) {
		return DateUtils.currentDate(foramt);
	}

	public static long randomVal(Integer val) {
		return java.lang.Math.round(java.lang.Math.random() * val);
	}

	public static String contextPath(HttpServletRequest request) {
		return request.getContextPath();
	}

	public static String loginUrl(HttpServletRequest request) {
		return request.getContextPath()+"/login_check";
	}

	public static String logoutUrl(HttpServletRequest request) {
		return request.getContextPath()+"/logout";
	}
	
	public static boolean isPasswordResetModeManager() {
		return VartoolConfiguration.getInstance().getPasswordResetMode().equals(VartoolConstants.PASSWORD_RESET_MODE.MANAGER);
	}
	
	public static boolean isPasswordResetModeEmail() {
		return VartoolConfiguration.getInstance().getPasswordResetMode().equals(VartoolConstants.PASSWORD_RESET_MODE.EMAIL);
	}
}
