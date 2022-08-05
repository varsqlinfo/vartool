package com.vartool.web.template;

import java.io.IOException;
import java.util.Objects;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

/**
 * handlebars helpers
* 
* @fileName	: HandlebarsHelpers.java
* @author	: ytkim
 */
public enum HandlebarsHelpers implements Helper<Object> {
	xif{
		public Object apply(Object arg0, Options options) throws IOException {
			Object obj1 = options.param(0);
			Object obj2 = options.param(1);
			if ("==".equals(obj1))
				return Objects.equals(arg0, obj2) ? options.fn() : options.inverse();
			if ("!=".equals(obj1))
				return !Objects.equals(arg0, obj2) ? options.fn() : options.inverse();
			if ("<".equals(obj1))
				return (Integer.parseInt(arg0.toString()) < Integer.parseInt(obj2.toString())) ? options.fn()
						: options.inverse();
			if ("<=".equals(obj1))
				return (Integer.parseInt(arg0.toString()) <= Integer.parseInt(obj2.toString())) ? options.fn()
						: options.inverse();
			if (">".equals(obj1))
				return (Integer.parseInt(arg0.toString()) > Integer.parseInt(obj2.toString())) ? options.fn()
						: options.inverse();
			if (">=".equals(obj1))
				return (Integer.parseInt(arg0.toString()) >= Integer.parseInt(obj2.toString())) ? options.fn()
						: options.inverse();
			return options.inverse();
		}
	},
	equals {
		public Object apply(Object obj1, Options options) throws IOException {
			Object obj2 = options.param(0);
	        //System.out.println(obj1+" :: "+obj2 +" :: "+ options.fn()+" :: "+ options.inverse());
	        return Objects.equals(obj1, obj2) ? options.fn() : options.inverse();
		}
	}
	,ne {
		public Object apply(Object obj1, Options options) throws IOException {
			Object obj2 = options.param(0);
	    	return !Objects.equals(obj1, obj2);
		}
	}
}