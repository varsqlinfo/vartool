package com.vartool.web.template;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.vartool.web.app.config.ConfigProp;
import com.vartool.web.constants.VartoolConstants;
import com.vartool.web.module.ResourceUtils;

/**
 * ant build.xml template 생성.
 * @author ytkim
 *
 */
public final class AntBuildTemplate {
	private static String BUILD_TEMPLATE;
	private static Handlebars handlebars = new Handlebars();
	
	static{
		for (HandlebarsHelpers helper : HandlebarsHelpers.values()) {
		     handlebars.registerHelper(helper.name(), helper); 
		}
		
		try {
			Resource resource = ResourceUtils.getResource(ConfigProp.jdf_file_directory+"build_template.template");
			BUILD_TEMPLATE = FileUtils.readFileToString(resource.getFile(), VartoolConstants.CHAR_SET);
		} catch (IOException e) {
			throw new Error("template not found path: "+ ConfigProp.jdf_file_directory+"build_template.template");
		}
	}
	
	public static String getBuildXml(Map param) throws IOException{
		Template template = handlebars.compileInline(BUILD_TEMPLATE);
		
		String retVal = template.apply(param);
		return retVal; 
	}
	
	public static void main(String[] args) {
		Map param = new HashMap();
		
		List projects = new ArrayList();
		
		projects.add(new HashMap(){{
			put("prjectName", "111");
			put("deployPath", "222");
			put("srcPath", "333");
			put("libPath", "444");
		}});
		
		projects.add(new HashMap(){{
			put("prjectName", "555555555555");
			put("deployPath", "222");
			put("srcPath", "333");
			put("libPath", "4awefawefawef44");
		}});
		
		param.put("projects", projects);
		try {
			System.out.println(getBuildXml(param));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("222222");
	}
}

