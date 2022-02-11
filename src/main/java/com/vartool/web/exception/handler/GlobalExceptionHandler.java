package com.vartool.web.exception.handler;

import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.util.NestedServletException;

import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.constants.RequestResultCode;
import com.vartech.common.utils.HttpUtils;
import com.vartech.common.utils.VartechUtils;
import com.vartool.web.constants.VartoolConstants;
import com.vartool.web.exception.DataDownloadException;
import com.vartool.web.exception.VartoolAppException;
import com.vartool.web.module.SecurityUtil;
import com.vartool.web.module.VartoolUtils;

/**
 *
*-----------------------------------------------------------------------------
* @PROJECT	: varsql
* @NAME		: GlobalExceptionHandler.java
* @DESC		: exception handler
* @AUTHOR	: ytkim
*-----------------------------------------------------------------------------
  DATE			AUTHOR			DESCRIPTION
*-----------------------------------------------------------------------------
* 2019. 4. 16. 			ytkim			최초작성

*-----------------------------------------------------------------------------
 */
@ControllerAdvice
public class GlobalExceptionHandler{

	private final Logger logger = LoggerFactory.getLogger("appErrorLog");

	/**
	 *
	 * @Method Name  : sqlExceptionHandle
	 * @Method 설명 : sql exception 처리.
	 * @작성자   : ytkim
	 * @작성일   : 2017. 11. 13.
	 * @변경이력  :
	 * @param ex
	 * @param response
	 * @return
	 */
	@ExceptionHandler(value=SQLException.class)
	public void sqlExceptionHandler(SQLException ex, HttpServletRequest request ,  HttpServletResponse response){

		logger.error("sqlExceptionHandler url : {}, parameter : {} ",request.getRequestURL(), HttpUtils.getServletRequestParam(request));
		logger.error("sqlExceptionHandler :{} ", ex.getMessage() , ex);

		ResponseResult result = new ResponseResult();
		result.setResultCode(RequestResultCode.ERROR);
		result.setMessage(ex.getMessage());

		exceptionRequestHandle(ex, request, response ,result,"connError");
	}


	/**
	 *
	 * @Method Name  : varsqlAppExceptionHandler
	 * @Method 설명 : var sql error 처리.
	 * @작성자   : ytkim
	 * @작성일   : 2017. 11. 13.
	 * @변경이력  :
	 * @param ex
	 * @param response
	 * @return
	 */
	@ExceptionHandler(value=VartoolAppException.class)
	public void varsqlAppExceptionHandler(VartoolAppException ex, HttpServletRequest request , HttpServletResponse response){

		logger.error("varsqlAppExceptionHandler url : {}, parameter : {} ",request.getRequestURL(), HttpUtils.getServletRequestParam(request));
		logger.error("varsqlAppExceptionHandler :{} ", ex.getMessage() , ex);

		ResponseResult result = new ResponseResult();
		result.setResultCode(RequestResultCode.ERROR);
		result.setMessageCode(ex.getMessageCode());
		result.setMessage(ex.getMessage());

		exceptionRequestHandle(ex, request, response ,result);
	}

	/**
	 *
	 * @Method Name  : runtimeExceptionHandle
	 * @Method 설명 : 실행시 에러 처리.
	 * @작성자   : ytkim
	 * @작성일   : 2017. 11. 13.
	 * @변경이력  :
	 * @param ex
	 * @param response
	 * @return
	 */
	@ExceptionHandler(value=RuntimeException.class)
	public void runtimeExceptionHandler(RuntimeException ex, HttpServletRequest request , HttpServletResponse response){

		logger.error("runtimeExceptionHandle url : {}, parameter : {} ",request.getRequestURL(), HttpUtils.getServletRequestParam(request));
		logger.error("runtimeExceptionHandle :{} ", ex.getMessage() , ex);

		ResponseResult result = new ResponseResult();
		result.setMessage(ex.getMessage());

		exceptionRequestHandle(ex, request, response ,result);
	}

	/**
	 *
	 * @Method Name  : classNotFoundExceptionHandler
	 * @Method 설명 : class  not found exception 처리.
	 * @작성자   : ytkim
	 * @작성일   : 2019. 9. 7.
	 * @변경이력  :
	 * @param ex
	 * @param request
	 * @param response
	 */
	@ExceptionHandler(value= {ClassNotFoundException.class, NoClassDefFoundError.class})
	public void classExceptionHandler(Exception ex, HttpServletRequest request , HttpServletResponse response){

		logger.error("classExceptionHandler url : {}, parameter : {} ",request.getRequestURL(), HttpUtils.getServletRequestParam(request));
		logger.error("classExceptionHandler : {} ", ex.getMessage() ,ex);
		ResponseResult result = new ResponseResult();
		result.setMessage(ex.getMessage());
		exceptionRequestHandle(ex, request, response ,result);
	}

	@ExceptionHandler(value=Exception.class)
	public void exceptionHandler(Exception ex,HttpServletRequest request ,  HttpServletResponse response){

		logger.error("exceptionHandler url : {}, parameter : {} ",request.getRequestURL(), HttpUtils.getServletRequestParam(request));
		logger.error("exceptionHandler :{} ", ex.getMessage() , ex);

		ResponseResult result = new ResponseResult();

		if(ex instanceof NestedServletException) {
			NestedServletException  nestedServletException= (NestedServletException)ex;

			Throwable throwable= nestedServletException.getRootCause();

			if(throwable instanceof NoClassDefFoundError || throwable instanceof ClassNotFoundException) {
				result.setMessage(ex.getMessage());
			}
		}

		exceptionRequestHandle(ex, request, response ,result);
	}

	/**
	 *
	 * @Method Name  : multipartexceptionHandler
	 * @Method 설명 : upload error
	 * @작성자   : ytkim
	 * @작성일   : 2019. 11. 29.
	 * @변경이력  :
	 * @param request
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(MultipartException.class)
    public @ResponseBody ResponseResult multipartexceptionHandler(HttpServletRequest request, Throwable ex) {

        HttpStatus status = getStatus(request);

        ResponseResult result = new ResponseResult();
        result.setResultCode(RequestResultCode.ERROR);
        result.setMessage(ex.getMessage());
        return result;
    }
	
	/**
	 *
	 * @Method Name  : noHandlerFoundExceptionHandler
	 * @Method 설명 : 404
	 * @작성자   : ytkim
	 * @작성일   : 2019. 11. 29.
	 * @변경이력  :
	 * @param request
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(NoHandlerFoundException.class)
    public void noHandlerFoundExceptionHandler(NoHandlerFoundException ex,HttpServletRequest request ,  HttpServletResponse response) {

		logger.error("handle404 url : {}, parameter : {} ",request.getRequestURL(), HttpUtils.getServletRequestParam(request));
		logger.error("handle404 :{} ", ex.getMessage() , ex);

		ResponseResult result = new ResponseResult();
		result.setMessage(ex.getMessage());
		exceptionRequestHandle(ex,request, response ,result , "error404");
    }

    /**
	 *
	 * @Method Name  : missingServletRequestParameterExceptionHandler
	 * @Method 설명 : missing parameter exception
	 * @작성자   : ytkim
	 * @작성일   : 2019. 11. 29.
	 * @변경이력  :
	 * @param request
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value=MissingServletRequestParameterException.class)
	public void missingServletRequestParameterExceptionHandler(Exception ex,HttpServletRequest request ,  HttpServletResponse response){

		logger.error("missingServletRequestParameterExceptionHandler:{}",ex.getMessage() , ex);

		exceptionRequestHandle(ex,request, response ,new ResponseResult(), "error403");
	}
	
	/**
	 *
	 * @Method Name  : dataDownloadExceptionHandler
	 * @Method 설명 : data download exception
	 * @작성자   : ytkim
	 * @작성일   : 2019. 11. 29.
	 * @변경이력  :
	 * @param request
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value=DataDownloadException.class)
	public void dataDownloadExceptionHandler(Exception ex,HttpServletRequest request ,  HttpServletResponse response){
		exceptionRequestHandle(ex,request ,response , new ResponseResult()  , "dataDownloadError");
	}

	private void exceptionRequestHandle(Exception ex, HttpServletRequest request, HttpServletResponse response ,ResponseResult result) {
		exceptionRequestHandle(ex,request ,response , result  , "error500");
	}

	private void exceptionRequestHandle(Exception ex, HttpServletRequest request, HttpServletResponse response ,ResponseResult result, String pageName) {
		if(VartoolUtils.isAjaxRequest(request)){
			response.setContentType(VartoolConstants.JSON_CONTENT_TYPE);
			response.setStatus(HttpStatus.OK.value());
			result.setResultCode(RequestResultCode.ERROR);

			if(!SecurityUtil.isAdmin()) {
				result.setMessage(result.getResultCode() + " :: " + ex.getClass());
			}

			try (Writer writer= response.getWriter()){
				writer.write(VartechUtils.objectToJsonString(result));
			} catch (IOException e) {
				logger.error("exceptionRequestHandle Cause :" + e.getMessage() ,e);
			}
		}else{
			
			request.setAttribute("errorMessage", ex.getMessage());
			try {
				RequestDispatcher dispatcher = request.getRequestDispatcher("/error/"+pageName);
				dispatcher.forward(request, response);
			} catch (ServletException | IOException e1) {
				logger.error("exceptionRequestHandle Cause :" + e1.getMessage() ,e1);
			}
		}
	}
	
	 private HttpStatus getStatus(HttpServletRequest request) {
	        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
	        if (statusCode == null) {
	            return HttpStatus.INTERNAL_SERVER_ERROR;
	        }
	        return HttpStatus.valueOf(statusCode);
	    }
}
