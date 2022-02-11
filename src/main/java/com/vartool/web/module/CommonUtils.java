package com.vartool.web.module;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;

import com.vartool.web.dto.vo.ClientPcInfo;

public final class CommonUtils {
	private CommonUtils() {} 
	
	public static String getJavaHome () {
		Properties properties = System.getProperties();
		return properties.getProperty("java.home"); 
	}
	
	public static String getOsName() {
		Properties properties = System.getProperties();
		return properties.getProperty("os.name"); 
	}
	
	public static ClientPcInfo getClientPcInfo(HttpServletRequest request) {
		String userAgent = "Unknown";
	    String osType = "Unknown";
	    String browser = "Unknown";
	    String deviceType = "pc";

        userAgent = request.getHeader("User-Agent");

        if (userAgent.indexOf("Windows NT") >= 0) {
            osType = "Windows";
        } else if (userAgent.indexOf("Mac OS") >= 0) {
            osType = "Mac";
            
            if(userAgent.indexOf("iPhone") >= 0) {
                deviceType = "iPhone";
            } else if(userAgent.indexOf("iPad") >= 0) {
                deviceType = "iPad";
            }
            
        } else if (userAgent.indexOf("X11") >= 0) {
            osType = "Unix";
        } else if (userAgent.indexOf("android") >= 0) {
            osType = "Android";
            deviceType = "Android";
        }
        
        String userAgentLower = userAgent.toLowerCase();
        
        if (userAgentLower.contains("msie") || userAgentLower.contains("rv")) {
        	browser= "msie";
        } else if (userAgentLower.contains("safari") && userAgentLower.contains("version")) {
        	browser= "Safari";
        } else if (userAgentLower.contains("opr") || userAgentLower.contains("opera")) {
        	browser= "opera";
        } else if(userAgentLower.contains("edge")){
            browser = "edge";
        } else if (userAgentLower.contains("chrome")) {
        	browser= "chrome";
        } else if ((userAgentLower.indexOf("mozilla/7.0") > -1) || (userAgentLower.indexOf("netscape6") != -1) || (userAgentLower.indexOf(
                "mozilla/4.7") != -1) || (userAgentLower.indexOf("mozilla/4.78") != -1) || (userAgentLower.indexOf(
                "mozilla/4.08") != -1) || (userAgentLower.indexOf("mozilla/3") != -1)) {
            browser = "Netscape";
        } else if (userAgentLower.contains("firefox")) {
        	browser= "firefox";
        } else{
            browser = "UnKnown, More-Info: " + userAgentLower;
        }

        ClientPcInfo cpi = new ClientPcInfo();
        
        cpi.setUserAgent(userAgent);
        cpi.setOsType(osType);
        cpi.setDeviceType(deviceType);
        cpi.setBrowser(browser.toLowerCase());
        cpi.setIp(getClientIp(request));
        return cpi;
	}
	
	/**
	 * 
	 * @Method Name  : getClientIp
	 * @Method 설명 : ip 정보. 
	 * @작성일   : 2019. 9. 21. 
	 * @작성자   : ytkim
	 * @변경이력  :
	 * @param req
	 * @return
	 */
	public static String getClientIp(HttpServletRequest req) {

		String[] headerKeyArr = {  "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP" 
				,"HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED"
				,"HTTP_FORWARDED_FOR", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_FORWARDED"
		};

		for (String headerKey : headerKeyArr) {
			String ip = req.getHeader(headerKey);

			if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
				return ip;
			}
		}

		return req.getRemoteAddr();
	}
	
	public static boolean isIE(ClientPcInfo clientPcInfo) {
		return "msie".equalsIgnoreCase(clientPcInfo.getBrowser());
	}
	
	public static boolean isChrome(ClientPcInfo clientPcInfo) {
		return "chrome".equalsIgnoreCase(clientPcInfo.getBrowser());
	}
	
	public static boolean isFirefox(ClientPcInfo clientPcInfo) {
		return "firefox".equalsIgnoreCase(clientPcInfo.getBrowser());
	}
	
	public static boolean isSafari(ClientPcInfo clientPcInfo) {
		return "safari".equalsIgnoreCase(clientPcInfo.getBrowser());
	}
	
	public static boolean isOpera(ClientPcInfo clientPcInfo) {
		return "opera".equalsIgnoreCase(clientPcInfo.getBrowser());
	}

	public static String getDownloadFileName(HttpServletRequest req, String downFileName) throws UnsupportedEncodingException {
		ClientPcInfo clientInfo = getClientPcInfo(req);

		if (isIE(clientInfo)) {
			downFileName = URLEncoder.encode(downFileName, "UTF-8").replaceAll("\\+", "%20");
		}else if(isFirefox(clientInfo)){
			downFileName = "\""+new String(downFileName.getBytes("UTF-8"), "ISO-8859-1")+"\"";
		}else if(isChrome(clientInfo)){
			downFileName = URLEncoder.encode(downFileName, "UTF-8").replaceAll("\\+", "%20");
		}else if(isSafari(clientInfo)){
			downFileName = "\""+new String(downFileName.getBytes("UTF-8"), "ISO-8859-1")+"\"";
		}else if(isOpera(clientInfo)){
			downFileName = "\""+new String(downFileName.getBytes("UTF-8"), "ISO-8859-1")+"\"";
		}else {
			downFileName = URLEncoder.encode(downFileName, "UTF-8").replaceAll("\\+", "%20");
		}
		
		return downFileName;
	}
}
