package com.vartool.web.constants;

/**
 * view page url info
* 
* @fileName	: VIEW_PAGE.java
* @author	: ytkim
 */
public enum VIEW_PAGE{
	 LOGIN("/login")
	, JOIN("/join")
	, GUEST("/guest")
	, COMMONPAGE("/common")
	, USER_PREFERENCES("/user/preferences")
	, USER("/user")
	, MGMT("/mgmt")
	, ADMIN("/admin");

	private String jspPath;

	VIEW_PAGE(String prefix){
		this.jspPath = prefix;
	}

	public String getPrefix() {
		return jspPath;
	}

	public String getViewPage(String path) {
		return String.format("%s%s", jspPath, path);
	}
}