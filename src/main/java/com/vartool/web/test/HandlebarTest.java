package com.vartool.web.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.vartool.web.module.ResourceUtils;
import com.vartool.web.template.DeploySourceReplaceUtils;

public class HandlebarTest {
	public static void main(String[] args) {
		
		String buildSource;
		try {
			buildSource = ResourceUtils.getResourceString("config/antXmlSample.template");
			
			Map replaceInfo = new HashMap();
			
			replaceInfo.put("sourcePath", "sourcePathsourcePath");
			replaceInfo.put("deployPath", "deployPathdeployPath");
			
			buildSource = DeploySourceReplaceUtils.getInstance().getBuildSource(buildSource, replaceInfo);
			
			System.out.println(buildSource);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
