package com.vartool.web.module;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.util.UrlPathHelper;

import com.vartech.common.app.beans.DataMap;
import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.app.beans.SearchParameter;
import com.vartech.common.constants.CodeEnumValue;
import com.vartech.common.constants.RequestResultCode;
import com.vartech.common.utils.PagingUtil;
import com.vartool.web.constants.VartoolConstants;
import com.vartool.web.dto.websocket.LogMessageDTO;
import com.vartool.web.model.converter.DomainMapper;

public final class VartoolUtils {
	public static boolean isAjaxRequest(HttpServletRequest request) {
		String headerInfo = request.getHeader("X-Requested-With");
		if ("XMLHttpRequest".equals(headerInfo))
			return true;
		return false;
	}

	public static void setResponseDownAttr(HttpServletResponse res, String fileName) {
		res.setContentType("application/octet-stream");
		res.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\";", new Object[] { fileName }));
		res.setCharacterEncoding(VartoolConstants.CHAR_SET);
		res.setHeader("Content-Transfer-Encoding", "binary;");
		res.setHeader("Pragma", "no-cache;");
		res.setHeader("Expires", "-1;");
	}

	public static void textDownload(OutputStream output, String cont) throws IOException {
		try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(output, VartoolConstants.CHAR_SET))) {
			out.write(cont);
			out.newLine();
			out.close();
		}
	}

	public static Timestamp getTimestamp(long time) {
		return new Timestamp(time);
	}

	public static LocalDateTime getLocalDateTime(long time) {
		return Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public static LocalDate getLocalDate(long time) {
		return Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static String getClientIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getHeader("Proxy-Client-IP");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getHeader("WL-Proxy-Client-IP");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getHeader("HTTP_CLIENT_IP");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getRemoteAddr();
		return ip;
	}

	public static ResponseResult getResponseResultItemOne(Object obj) {
		ResponseResult responseResult = new ResponseResult();
		responseResult.setItemOne(obj);
		return responseResult;
	}

	public static ResponseResult getResponseResultItemList(List<?> list) {
		ResponseResult responseResult = new ResponseResult();
		responseResult.setItemList(list);
		return responseResult;
	}

	public static String mapToJsonObjectString(Map<String, String> info) {
		StringBuffer returnVal = new StringBuffer();
		boolean firstFlag = true;
		returnVal.append("{");
		for (Map.Entry<String, String> item : info.entrySet()) {
			String key = item.getKey();
			String prefVal = item.getValue();
			returnVal.append(firstFlag ? "" : ",").append("\"").append(key).append("\":").append(prefVal);
			firstFlag = false;
		}
		returnVal.append("}");
		return returnVal.toString();
	}

	public static ResponseResult getResponseResultValidItem(BindingResult result) {
		return getResponseResultValidItem(new ResponseResult(), result);
		
	}
	public static ResponseResult getResponseResultValidItem(ResponseResult resultObject, BindingResult result) {
		resultObject.setResultCode((CodeEnumValue) RequestResultCode.DATA_NOT_VALID);
		List<FieldError> fieldErrors = result.getFieldErrors();
		String errorMessage = "";
		if (fieldErrors.size() > 0) {
			FieldError errorInfo = fieldErrors.get(0);
			errorMessage = String.format("field : %s\nmessage : %s\nvalue : %s",
					new Object[] { errorInfo.getField(), errorInfo.getDefaultMessage(), errorInfo.getRejectedValue() });
			resultObject.setItemOne(errorInfo.getField());
		} else {
			List<ObjectError> allErrors = result.getAllErrors();
			if (allErrors.size() > 0) {
				ObjectError errorInfo = allErrors.get(0);
				resultObject.setItemOne(errorInfo.getCode());
				errorMessage = String.format("code : %s, message : %s",
						new Object[] { errorInfo.getCode(), errorInfo.getDefaultMessage() });
			} else {
				resultObject.setItemOne(result.getGlobalError().getCode());
				errorMessage = String.format("code : %s, message : %s", new Object[] {
						result.getGlobalError().getCode(), result.getGlobalError().getDefaultMessage() });
			}
		}
		resultObject.setMessage(errorMessage);
		return resultObject;
	}
	
	public static String [] getNotyRecvIds (DataMap param) {
		return param.getString("recv_ids","").split(";");
	}
	
	public static String getDeployRecvId (String uid) {
		return  uid;
	}
	
	public static String getCommandRecvId (String uid) {
		return  uid;
	}
	
	public static String getAppRecvId (String uid) {
		return  uid;
	}

	public static LogMessageDTO setRunningUserInfo(LogMessageDTO lmd, Map<String, String> userInfo) {
		lmd.addItem("userId", userInfo.get("userId"));
		lmd.addItem("userIp", userInfo.get("userIp"));
		return lmd;
	}

	/**
	 * @method  : convertSearchInfoToPage
	 * @desc : searchparameter 를 페이지 정보로  변환.
	 * @author   : ytkim
	 * @date   : 2020. 4. 14.
	 * @param searchInfo
	 * @return
	 */
	public static org.springframework.data.domain.Pageable convertSearchInfoToPage(SearchParameter searchInfo) {
		return convertSearchInfoToPage(searchInfo, "regDt");
	}
	
	public static org.springframework.data.domain.Pageable convertSearchInfoToPage(SearchParameter searchInfo, String ... sort) {
		return org.springframework.data.domain.PageRequest.of(searchInfo.getPageNo() -1, searchInfo.getCountPerPage(), searchInfo.isSortAscFlag() ? Sort.Direction.ASC : Sort.Direction.DESC, sort);
	}
	
	public static org.springframework.data.domain.Pageable convertSearchInfoToPage(SearchParameter searchInfo, Sort sort) {
		return org.springframework.data.domain.PageRequest.of(searchInfo.getPageNo() -1, searchInfo.getCountPerPage(), sort);
	}
	
	public static ResponseResult getResponseResult(Page<?> result, SearchParameter searchParameter) {
		ResponseResult responseResult = new ResponseResult();
		responseResult.setItemList(result.getContent());
		responseResult.setPage(PagingUtil.getPageObject(result.getTotalElements(), searchParameter));
		return responseResult;
	}

	public static ResponseResult getResponseResult(Page<?> result, SearchParameter searchParameter, DomainMapper domainMapper, Class<?> mapperClass) {
		ResponseResult responseResult = new ResponseResult();
		responseResult.setItemList(result.getContent().stream().map(item -> domainMapper.convertToDomain(item, mapperClass)).collect(Collectors.toList()));
		responseResult.setPage(PagingUtil.getPageObject(result.getTotalElements(), searchParameter));
		return responseResult;
	}

	public static ResponseResult getResponseResult(List <?> result, DomainMapper domainMapper, Class<?> mapperClass) {
		ResponseResult responseResult = new ResponseResult();
		responseResult.setItemList(result.stream().map(item -> domainMapper.convertToDomain(item, mapperClass)).collect(Collectors.toList()));
		return responseResult;
	}

	public static ResponseResult getResponseResult(List <?> result, long totalCount , SearchParameter searchParameter) {
		ResponseResult responseResult = new ResponseResult();
		responseResult.setItemList(result);
		responseResult.setPage(PagingUtil.getPageObject(totalCount, searchParameter));
		return responseResult;
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
	
	public static String getOriginatingRequestUri(HttpServletRequest req){
		return new UrlPathHelper().getOriginatingRequestUri(req);
	}
}
