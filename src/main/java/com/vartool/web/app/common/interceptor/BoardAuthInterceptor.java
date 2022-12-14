package com.vartool.web.app.common.interceptor;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import com.vartech.common.utils.StringUtils;
import com.vartool.web.constants.AppCode;
import com.vartool.web.exception.BoardInvalidException;
import com.vartool.web.module.SecurityUtils;

/**
 * 게시판 check 인터 셉터.
* 
* @fileName	: BoardAuthInterceptor.java
* @author	: ytkim
 */
public class BoardAuthInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler)
			throws ServletException, IOException {
		
		Map pathVariables = (Map) req.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		
		String boardId = null; 
		if(pathVariables.containsKey(AppCode.BOARD_CODE)) {
			boardId = String.valueOf(pathVariables.get(AppCode.BOARD_CODE));
			
			req.setAttribute(AppCode.BOARD_CODE, boardId);
			
			if(SecurityUtils.isAdmin()) {
				return true;
			}
			
			if(StringUtils.isBlank(boardId)) {
				return false; 
			}
			
			if (authCheck(req, boardId)) {
				return true;
			}
		}
		
		throw new BoardInvalidException(String.format("Board invalid request userViewId : %s , conuid : %s", SecurityUtils.userViewId(), boardId));
		
	}
	
	/**
	 * 
	 * @Method Name  : authCheck
	 * @Method 설명 : Board 관련 권한 체크. 
	 * @작성일   : 2015. 6. 22. 
	 * @작성자   : ytkim
	 * @변경이력  :  
	 * 			/Board/base 밑이면 conuid만 체크 
	 * 			각 데이터베이스 벤더의 data를 콜하는 경우면 Board type까지 체크. 
	 * @param req
	 * @param connid
	 * @return
	 */
	private boolean authCheck(HttpServletRequest req, String conuid) {
		return true; 
	}
}
