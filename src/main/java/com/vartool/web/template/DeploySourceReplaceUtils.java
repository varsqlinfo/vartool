package com.vartool.web.template;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

public final class DeploySourceReplaceUtils {
	private final Logger logger = LoggerFactory.getLogger(DeploySourceReplaceUtils.class);

	private Handlebars handlebars;

	private DeploySourceReplaceUtils() {
		this.handlebars = new Handlebars();
		this.handlebars.setPrettyPrint(true);
		for (HandlebarsHelpers helper : HandlebarsHelpers.values()) {
		      this.handlebars.registerHelper(helper.name(), helper); 
		}
	}

	private static class FactoryHolder {
		private static final DeploySourceReplaceUtils instance = new DeploySourceReplaceUtils();
	}

	public static DeploySourceReplaceUtils getInstance() {
		return FactoryHolder.instance;
	}
	
	public String getBuildSource(String buildSource, Map param) throws IOException{
		Template template = handlebars.compileInline(buildSource);
		
		String retVal = template.apply(param);
		return retVal; 
	}
}
