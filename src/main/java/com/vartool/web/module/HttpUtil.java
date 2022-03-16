package com.vartool.web.module;


import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.UrlPathHelper;

import com.vartech.common.app.beans.ParamMap;


/**
 * 
 * @FileName : HttpUtil.java
 * @작성자 	 : ytkim
 * @Date	 : 2013. 10. 18.
 * @프로그램설명:
 * @변경이력	:
 */
public class HttpUtil {
	
	private final static String CHAR_SET = "UTF-8";
	
	/**
	 * You can't call the constructor.
	 */
	private HttpUtil() {}
	/**
	 * Decode a string from <code>x-www-form-urlencoded</code> format.
	 *
	 * @param   s   an encoded <code>String</code> to be translated.
	 * @return  the original <code>String</code>.
	 * @throws UnsupportedEncodingException 
	 * @see		java.net.URLEncoder#encode(java.lang.String)
	 */
	public static String decode(String s) throws UnsupportedEncodingException {
		return decode(s,CHAR_SET);
	}
	public static String decode(String s,String charset) throws UnsupportedEncodingException {
		return java.net.URLEncoder.encode(s,charset);
	}
	/**
	 * Translates a string into <code>x-www-form-urlencoded</code> format.
	 *
	 * @param   s   <code>String</code> to be translated.
	 * @return  the translated <code>String</code>.
	 * @throws UnsupportedEncodingException 
	 * @see		java.net.URLEncoder#encode(java.lang.String)
	 */
	public static String encode(String s) throws UnsupportedEncodingException {
		return java.net.URLEncoder.encode(s, CHAR_SET);
	}
	
	/**
	 * @param req javax.servlet.http.HttpServletRequest
	 * @param name ParamMap name for this SessionBox
	 */
	public static ParamMap getAllParameter(HttpServletRequest req)  {
		ParamMap ParamMap = new ParamMap();

		Enumeration e = req.getParameterNames();
		while(e.hasMoreElements()){
			String key = (String)e.nextElement();
			ParamMap.put(key, req.getParameter(key));
		}
		
		ParamMap.put("_userip" , getClientIP(req));
		return ParamMap;
	}
	
	public static ParamMap getParameter(HttpServletRequest req)  {
		ParamMap ParamMap = new ParamMap();
		
		Enumeration e = req.getParameterNames();
		while(e.hasMoreElements()){
			String key = (String)e.nextElement();
			ParamMap.put(key, req.getParameter(key));
		}
		return ParamMap;
	}
	
	/**
	 * 
	 * @Method Name : getInt
	 * @작성자 : ytkim
	 * @작성일 : 2013. 10. 18.
	 * @Method설명 :
	 */
	public static String getString(HttpServletRequest req , String name)  {
		return getString(req , name ,"");
	}
	
	public static String getString(HttpServletRequest req , String name , String initval)  {
		String v = req.getParameter(name);
		
		return (v = v==null || "".equals(v)? initval: v); 
	}
	
	/**
	 * 
	 * @Method Name : getInt
	 * @작성자 : ytkim
	 * @작성일 : 2013. 10. 18.
	 * @Method설명 :
	 */
	public static String[] getStringValues(HttpServletRequest req , String name)  {
		String[] v = req.getParameterValues(name);
		
		return (v = v==null || "".equals(v)? new String[0]: v); 
	}
	
	public static String[] getStringValues(HttpServletRequest req , String name , String[] initval)  {
		String[] v = req.getParameterValues(name);
		
		return (v = v==null || "".equals(v)? initval: v); 
	}
	
	/**
	 * 
	 * @Method Name : getInt
	 * @작성자 : ytkim
	 * @작성일 : 2013. 10. 18.
	 * @Method설명 :
	 */
	public static int getInt(HttpServletRequest req , String name)  {
		return getInt(req, name, -1);
	}
	public static int getInt(HttpServletRequest req , String name, int initval)  {
		String v = getString(req,name);
		
		try{
			return Integer.parseInt(v);
		}catch(Exception e){
			return initval; 
		}
	}
	
	/*
	 * @return boolean
	 * @param req HttpServletRequest
	 */
	public static boolean isOverIE50(HttpServletRequest req) {
		String user_agent = (String) req.getHeader("user-agent");

		if ( user_agent == null ) 	return false;

		int index = user_agent.indexOf("MSIE");
		if ( index == -1 ) return false;

		int version = 0;
		try {
			version = Integer.parseInt(user_agent.substring(index+5, index+5+1));
		}
		catch(Exception e){}
		if ( version < 5 ) return false;

		return true;
	}
	
	/**
	 * 
	 * @param req
	 * @return
	 */
	public static String getOriginatingRequestUri(HttpServletRequest req){
		return new UrlPathHelper().getOriginatingRequestUri(req);
	}
	
	public static String getClientIP(HttpServletRequest request) {

	     String ip = request.getHeader("X-FORWARDED-FOR"); 
	     
	     if (ip == null || ip.length() == 0) {
	         ip = request.getHeader("Proxy-Client-IP");
	     }

	     if (ip == null || ip.length() == 0) {
	         ip = request.getHeader("WL-Proxy-Client-IP");  // 웹로직
	     }

	     if (ip == null || ip.length() == 0) {
	         ip = request.getRemoteAddr() ;
	     }
	     
	     return ip;
	}
	
	public static String getDomain(HttpServletRequest req) {
		
		String connDomain = req.getServerName();
        String nameArr [] = connDomain.split("\\.");
        
        if(nameArr.length > 1 ) {
        	connDomain = nameArr[nameArr.length-2]+"."+nameArr[nameArr.length-1];
        }
		
		return connDomain;
	}
	
	public static String getDomain(HttpServletRequest req , String prefix) {
		return getDomain(req, prefix, false);
	}
	
	public static String getDomain(HttpServletRequest req , String prefix, boolean protocalFlag) {
		
		String connDomain = req.getServerName();
		String nameArr [] = connDomain.split("\\.");
		
		if(nameArr.length > 1 ) {
			connDomain = nameArr[nameArr.length-2]+"."+nameArr[nameArr.length-1];
		}
		
		return (protocalFlag?req.getScheme()+"://":"")+prefix+"."+connDomain;
	}
	public static boolean isAjaxRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return false;
	}
	
}