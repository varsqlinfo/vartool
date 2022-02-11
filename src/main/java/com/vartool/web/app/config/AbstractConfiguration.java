package com.vartool.web.app.config;

import java.io.File;

public abstract class AbstractConfiguration {
	
	public static String getConfigRootPath() {
		String installRoot = System.getProperty("vartool.root");
		if (installRoot == null || "".equals(installRoot)) {
			String catalinaHome = System.getProperty("catalina.home", "");
			if (!"".equals(catalinaHome)) {
				installRoot = System.getProperty("catalina.home") + File.separator + "vtool";
			} else {
				installRoot = Thread.currentThread().getContextClassLoader().getResource("").getFile();
			}
		}
		installRoot = installRoot.replaceAll("\\\\", "/");
		if (installRoot.endsWith("/")) {
			installRoot = installRoot.substring(0, installRoot.length() - 1);
		}
		
		if(!new File(installRoot).exists()) {
			new File(installRoot).mkdir();
		}
		return installRoot;
	}
}
