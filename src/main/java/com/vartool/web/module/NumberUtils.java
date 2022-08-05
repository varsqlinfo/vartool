package com.vartool.web.module;

/**
 * number utils
* 
* @fileName	: NumberUtils.java
* @author	: ytkim
 */
public final class NumberUtils {
	private NumberUtils() {} 
	
	public static boolean isNullOrZero(Double val) {
		if(val == null) return true; 
		return val.longValue() ==0;
	}
	
	public static boolean isNullOrZero(Long val) {
		if(val == null) return true; 
		return val.longValue() ==0; 
	}
	
	public static boolean isNullOrZero(Float val) {
		if(val == null) return true; 
		return val.floatValue() ==0; 
	}
	
	public static boolean isNullOrZero(Integer val) {
		if(val == null) return true; 
		return val.intValue() ==0; 
	}
}
