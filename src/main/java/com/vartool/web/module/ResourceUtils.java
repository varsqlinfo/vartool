package com.vartool.web.module;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.vartool.core.config.VartoolConfiguration;
import com.vartool.web.constants.VartoolConstants;

/**
 * resource load util
* 
* @fileName	: ResourceUtils.java
* @author	: ytkim
 */
public final class ResourceUtils {

	public final static String FILE_PREFIX = "file:";
	public final static String CLASS_PREFIX = "classpath:";

	private ResourceUtils() {}

	/**
	 * get package resources
	 *
	 * @method : getPackageResources
	 * @param packagePath
	 * @return
	 * @throws IOException
	 */
	public static Resource[] getPackageResources(String packagePath) throws IOException {
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(Thread.currentThread().getContextClassLoader());
		return resolver.getResources(packagePath) ;
	}

	/**
	 * get resource
	 *
	 * @method : getResource
	 * @param resourcePath
	 * @return
	 * @throws IOException
	 */
	public static Resource getResource(String resourcePath){
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(Thread.currentThread().getContextClassLoader());

		Resource reval;
		if(resourcePath.startsWith(CLASS_PREFIX) || resourcePath.startsWith(FILE_PREFIX)) {
			reval = resolver.getResource(resourcePath);
		}else {
			if(new File(resourcePath).exists()) {
				reval = resolver.getResource(FILE_PREFIX+resourcePath);
			}else {
				reval = resolver.getResource(resourcePath);
			}
		}

		if(reval.exists()) {
			return reval;
		}else {
			return null;
		}
	}

	/**
	 * resource -> string
	 *
	 * @method : getResourceString
	 * @param resourcePath
	 * @return
	 * @throws IOException
	 */
	public static String getResourceString(String resourcePath) throws IOException {
		return getResourceString(getResource(resourcePath));
	}

	/**
	 * resource -> string
	 *
	 * @method : getResourceString
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public static String getResourceString(Resource resource) throws IOException {
		return getResourceString(resource, VartoolConstants.CHAR_SET);
	}
	
	/**
	 * resource -> string
	 *
	 * @method : getResourceString
	 * @param resource
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public static String getResourceString(Resource resource, String charset) throws IOException {
		return IOUtils.toString(resource.getInputStream(), charset);
	}
	
	/**
	 * 
	 *
	 * @method : getInstallPathResource
	 * @param resourcePath
	 * @return
	 * @throws IOException
	 */
	public static Resource getInstallPathResource(String resourcePath) throws IOException {
	    Resource configResource;
	    
	    File file = new File(VartoolConfiguration.getConfigRootPath(), resourcePath);
	    if (file.exists()) {
	      configResource = getResource(file.getPath());
	    } else {
	      configResource = getResource(resourcePath);
	    } 
	    return configResource;
	  }
}

