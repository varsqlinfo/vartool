package com.vartool.web.constants;

public enum VIEW_PAGE{
	 LOGIN("/login")
	, JOIN("/join")
	, GUEST("/guest")
	, COMMONPAGE("/common")
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