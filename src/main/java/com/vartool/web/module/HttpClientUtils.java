package com.vartool.web.module;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vartech.common.app.beans.ParamMap;
import com.vartech.common.app.beans.ResponseResult;

/**
*-----------------------------------------------------------------------------
* @PROJECT	: 포탈 프로젝트
* @NAME		: HttpClientUtils.java
* @DESC		: http client utils
* @author	: ytkim
* @Contents :
*-----------------------------------------------------------------------------
  DATE			AUTHOR			DESCRIPTION
*-----------------------------------------------------------------------------
*2018. 5. 15. 			ytkim			최초작성

*-----------------------------------------------------------------------------
 */
public final class HttpClientUtils {
	private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);
	
	private static String DEFAULT_CHARSET = "UTF-8"; 
	private static int HTTP_CONNECTION_TIMEOUT = 5; //second 
	
	public static void main(String[] args) {
		ParamMap httpParam =  new ParamMap();
		
		httpParam.put("url", "http://sdasfa.hwtest.com:8080/sfa/actBzFml.do?request=getCroslGrdSpc2&usrno=8916436");
		httpParam.put("method", "get");
		httpParam.put("param", "{\"aaa\":\"afe\",\"bbb\":\"ccc\"}");
		
		Map<String,String> cookieInfo = new HashMap<String,String>(){{
        	put("InitechEamUID", "8916953");
        	put("InitechEamUIP", "10.110.120.32");
        	put("InitechEamULAT", "1523438006");
        	put("InitechEamUTOA", "1");
        	put("InitechEamUHMAC", "zrNP+OnnoMiEEqEyxAG+Iw%3D%3D");
        }};
		
		try {
			httpParam.put("cookie", cookieInfo);
			//ResponseResult reval = new HttpClientUtils().httpData(null ,httpParam);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public static ResponseResult httpData(HttpServletRequest req, ParamMap httpParam) throws IOException {
		if("get".equalsIgnoreCase(httpParam.getString("method"))) {
			return httpGetData(req, httpParam);
		}else {
			return httpPostData(req, httpParam);
		}
	}
	
	/**
	 * 
	 * @Method Name  : httpGetData
	 * @Method 설명 : httpGetData 처리.
	 * @작성자   : ytkim
	 * @작성일   : 2018. 4. 13. 
	 * @Contents :   
	 * @변경이력  :
	 * @param req
	 * @param httpParam
	 * @return
	 * @throws IOException
	 */
	private static ResponseResult httpGetData(HttpServletRequest req , ParamMap httpParam) throws IOException {
		CloseableHttpClient client =null; 
		String url = httpParam.getString("url");
		try {
			
			client = getClient (req ,url, httpParam); 
			
			Map reqParam = getReqParam(httpParam);
			
			HttpGet get = new HttpGet(url);
			
			String contentType = httpParam.getString("contentType"); 
			if(StringUtils.isEmpty((contentType))) {
				get.setHeader("Accept",contentType);
				get.setHeader("Content-type",contentType);
			}
			
			logger.info("url :{} ;; param : {}" ,url , httpParam);
			
			if(reqParam != null) {
				url =url+(url.indexOf("?") > -1 ? "&":"?")+getParamString(reqParam);
			}
			
			return getResponseResultData(client.execute(new HttpGet(url)));
		}catch(IOException ioe) {
			logger.error("url :{} ;; param : {}" ,url , httpParam);
			logger.error("httpGetData {} ", ioe.getMessage() , ioe);
			ResponseResult responseResult = new ResponseResult();
			responseResult.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			return responseResult;
		}finally{
			if(client!=null) {
				client.close();
			}
		}
	}
	
	private static Map getReqParam(ParamMap httpParam) {
		if(httpParam.containsKey("param")) {
			try {
				return JsonUtils.stringToObject(httpParam.getString("param"));
			}catch(Exception e) {
				//return null;
				logger.error(e.getMessage(), e);
			}
		}
		
		return null;
		
	}

	/**
	 * 
	 * @Method Name  : httpPostData
	 * @Method 설명 : http client post
	 * @작성자   : ytkim
	 * @작성일   : 2018. 4. 13. 
	 * @Contents :   
	 * @변경이력  :
	 * @param req
	 * @param httpParam
	 * @return
	 * @throws IOException
	 */
	private static ResponseResult httpPostData(HttpServletRequest req , ParamMap httpParam) throws IOException {
		
		CloseableHttpClient client =null; 
		String url = httpParam.getString("url"); 
		try {
			
			client = getClient (req ,url, httpParam); 
			
			HttpPost post = new HttpPost(url);
			
			Map reqParam = getReqParam(httpParam);
			
			String charset = httpParam.getString("charset");
			
			charset = StringUtils.isEmpty(charset)?DEFAULT_CHARSET : charset;
			
			String contentType = httpParam.getString("contentType"); 
			if(!StringUtils.isEmpty((contentType))) {
				post.setHeader("Accept",contentType);
				post.setHeader("Content-type",contentType);
			}
			
			if(reqParam != null) {
				if("body".equalsIgnoreCase(httpParam.getString("field"))) {
					post.setEntity(new StringEntity(httpParam.getString("body"),charset));
				}else {
					post.setEntity(new UrlEncodedFormEntity(getParam(reqParam),charset));
				}
			}
			
			return getResponseResultData(client.execute(post));
		}catch(IOException ioe) {
			logger.error("url :{} ;; param : {}" ,url , httpParam);
			logger.error("httpGetData {} ", ioe.getMessage() , ioe);
			
			ResponseResult responseResult = new ResponseResult();
			responseResult.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			return responseResult;
			
		}finally{
			if(client!=null) {
				client.close();
			}
		}
	}
	
	/**
	 * 	
	 * @Method Name  : getResponseResultData
	 * @Method 설명 : response data 가져오기.
	 * @작성자   : ytkim
	 * @작성일   : 2018. 4. 13. 
	 * @Contents :   
	 * @변경이력  :
	 * @param response
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	private static ResponseResult getResponseResultData(CloseableHttpResponse response) throws ParseException, IOException {
		
		ResponseResult responseResult = new ResponseResult();
		
		responseResult.setStatus(response.getStatusLine().getStatusCode());
		responseResult.setItemOne(EntityUtils.toString(response.getEntity()));
		return responseResult;
	}
	
	public static CloseableHttpClient getClient(HttpServletRequest req, String url, ParamMap httpParam) throws UnsupportedEncodingException {
		
		CloseableHttpClient client; 
		Map cookieParam = (Map)httpParam.get("cookie");
		CookieStore cookieStore= null;
		String cookieDomain ="";
		if(req ==null) {
			cookieDomain = ".zzGain.com";
		}else {
			cookieDomain = "."+HttpUtil.getDomain(req);
		}
		 
		if(cookieParam != null ) {
			cookieStore= getCookieStore(cookieStore, cookieParam ,cookieDomain);
		}
		
		if(httpParam.getBoolean("ssoCookie")) {
			cookieStore= getCookieStore(cookieStore, req ,cookieDomain);
		}
		
		int timeout = httpParam.getInt("timeout", HTTP_CONNECTION_TIMEOUT)*1000; 
		
		RequestConfig config  = RequestConfig.custom()
				.setConnectTimeout(timeout)
				.setConnectionRequestTimeout(timeout)
				.setSocketTimeout(timeout).build();
		
		SSLConnectionSocketFactory socketFactory = null; 
		
		if(url.startsWith("https://")) {
			socketFactory = createSSLConnectionSocketFactory();
		}
		
		if(cookieStore!=null) {
			client =HttpClientBuilder.create().setDefaultCookieStore(cookieStore)
					.setDefaultRequestConfig(config).setSSLSocketFactory(socketFactory).build();
		}else {
			client = HttpClientBuilder.create().setDefaultRequestConfig(config).setSSLSocketFactory(socketFactory).build();
		}
		
		return client;
	}
	
	private static String getParamString(Map reqParam) {
		StringBuilder queryString = new StringBuilder();
		Iterator iter = reqParam.entrySet().iterator();
		boolean firstFlag = true; 
		while(iter.hasNext()) {
			Map.Entry entry= (Map.Entry)iter.next();
			queryString.append(firstFlag?"":"&").append(String.valueOf(entry.getKey())).append("=").append(String.valueOf(entry.getValue()));
			firstFlag =false; 
		}
		
		return queryString.toString(); 
	}
	
	/**
	 * 
	 * @Method Name  : getParam
	 * @Method 설명 : name value param
	 * @작성자   : ytkim
	 * @작성일   : 2018. 4. 11. 
	 * @Contents :   
	 * @변경이력  :
	 * @param param
	 * @return
	 */
	private static List<NameValuePair> getParam(Map param) {
		List<NameValuePair> nameValueList = new ArrayList<NameValuePair>();
		
		Iterator iter = param.entrySet().iterator();
		
		while(iter.hasNext()) {
			Map.Entry entry= (Map.Entry)iter.next();
			
			nameValueList.add(new BasicNameValuePair(String.valueOf(entry.getKey()), String.valueOf(entry.getValue())));
		}
		
		return nameValueList;
	}
	
	/**
	 * 
	 * @Method Name  : getStringEntity
	 * @Method 설명 : json param
	 * @작성자   : ytkim
	 * @작성일   : 2018. 4. 11. 
	 * @Contents :   
	 * @변경이력  :
	 * @param param
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static StringEntity getStringEntity(Map param) throws UnsupportedEncodingException {
		return new StringEntity(JsonUtils.objectToString(param));
	}
	
	/**
	 * 
	 * @Method Name  : getCookie
	 * @Method 설명 : cookie 
	 * @작성자   : ytkim
	 * @작성일   : 2018. 4. 11. 
	 * @Contents :   
	 * @변경이력  :
	 * @param cookieParam
	 * @param domain
	 * @param cookieStore 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static CookieStore getCookieStore(CookieStore cookieStore, Map cookieParam, String domain) throws UnsupportedEncodingException {
		return getCookieStore(cookieStore,cookieParam, domain,"/");
	}
	
	private static CookieStore getCookieStore(CookieStore cookieStore, Map cookieParam, String domain ,String path) throws UnsupportedEncodingException {
		cookieStore = cookieStore == null ? new BasicCookieStore():cookieStore;
		
		Iterator iter = cookieParam.entrySet().iterator();
		BasicClientCookie cookie = null;
		while(iter.hasNext()) {
			Map.Entry entry= (Map.Entry)iter.next();
			
			cookie = new BasicClientCookie(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
			cookie.setDomain(domain);
			cookie.setPath(path);
			cookieStore.addCookie(cookie);
		}
		return cookieStore;
	}
	
	private static CookieStore getCookieStore(CookieStore cookieStore, HttpServletRequest req, String cookieDomain) {
		return getCookieStore(cookieStore, req, cookieDomain, "/");
	}
	
	private static CookieStore getCookieStore(CookieStore cookieStore, HttpServletRequest req, String cookieDomain, String path) {
		
		cookieStore = cookieStore == null ? new BasicCookieStore():cookieStore;
		BasicClientCookie cookie = null;
		Cookie[] cookies = req.getCookies();
		for(Cookie tmpCookie : cookies) {
			cookie = new BasicClientCookie(tmpCookie.getName(), tmpCookie.getValue());
			cookie.setDomain(cookieDomain);
			cookie.setPath(path);
			cookieStore.addCookie(cookie);
		}
		return cookieStore;
	}
	
	/**
	 * 
	 * @Method Name  : createSSLConnectionSocketFactory
	 * @Method 설명 : ssl connection factory
	 * @작성자   : ytkim
	 * @작성일   : 2018. 4. 13. 
	 * @Contents :   
	 * @변경이력  :
	 * @return
	 */
	private static SSLConnectionSocketFactory createSSLConnectionSocketFactory() {
		return createSSLConnectionSocketFactory(false);
	}
	private static SSLConnectionSocketFactory createSSLConnectionSocketFactory(boolean strict) {
		SSLContextBuilder builder = SSLContexts.custom();
		
		try {
			builder.loadTrustMaterial(null , new TrustEverythingStrategy());
			
			X509HostnameVerifier verifer; 
			
			if(strict) {
				verifer = SSLConnectionSocketFactory.STRICT_HOSTNAME_VERIFIER;
			}else {
				verifer = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
			}
			return new SSLConnectionSocketFactory(builder.build() , new String[] {"TLSv1","TLSv1.2"}, null , verifer);
		} catch (KeyManagementException | NoSuchAlgorithmException e) {
			logger.error("createSSLConnectionSocketFactory KeyManagementException" ,e);
		} catch (KeyStoreException e) {
			logger.error("createSSLConnectionSocketFactory KeyStoreException" ,e);
		}
		return null; 
	}
}
