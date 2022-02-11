package com.vartool.web.security.auth;

public enum AuthorityType {
	GUEST(-1, "screen.guest", "/guest/")
	, USER(100, "screen.user", "/main/")
	, MANAGER(500, "screen.manager", "/mgmt/")
	, ADMIN(999, "screen.admin", "/admin/");

	int priority = -1;
	String i18N = "";
	String mainPage = "";

	AuthorityType(int _priority, String _i18N, String _mainPage) {
		this.priority = _priority;
		this.i18N = _i18N;
		this.mainPage = _mainPage;
	}

	public int getPriority() {
		return this.priority;
	}

	public String geti18N() {
		return this.i18N;
	}

	public String mainPage() {
		return mainPage;
	}

	public String getMainPage() {
		return mainPage;
	}
	
	public static AuthorityType getType(String type) {
		if (type != null) {
			for (AuthorityType authType : values()) {
				if (type.equalsIgnoreCase(authType.name()))
					return authType;
			}
		}
		return GUEST;
	}
}