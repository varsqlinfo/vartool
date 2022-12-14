package com.vartool.web.module;

import java.io.Serializable;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.context.ApplicationContext;

import com.vartool.web.configuration.ApplicationContextProvider;

/**
 * -----------------------------------------------------------------------------
* @fileName		: VartoolBeanUtils.java
* @desc		: bean util 
* @author	: ytkim
*-----------------------------------------------------------------------------
  DATE			AUTHOR			DESCRIPTION
*-----------------------------------------------------------------------------
*2020. 4. 27. 			ytkim			최초작성

*-----------------------------------------------------------------------------
 */
public class VartoolBeanUtils {
    public static Object getStringBean(String beanName) {
        ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
        return applicationContext.getBean(beanName);
    }
    
    /**
	 * 
	 * @Method Name  : copyEntity
	 * @Method 설명 : entity copy
	 * @작성일   : 2020. 12. 07. 
	 * @작성자   : ytkim
	 * @변경이력  :
	 * @return
	 */
    public static <T extends Serializable> T copyEntity(T source) {
    	return SerializationUtils.clone(source);
	}
}
