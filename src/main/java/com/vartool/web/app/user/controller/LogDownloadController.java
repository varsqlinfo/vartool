package com.vartool.web.app.user.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vartech.common.app.beans.DataMap;
import com.vartech.common.utils.HttpUtils;
import com.vartool.web.app.user.service.LogDownloadService;

@Controller
public class LogDownloadController {
	

	/** The Constant logger. */
	private final static Logger logger = LoggerFactory.getLogger(LogDownloadController.class);
	
	@Autowired
	private LogDownloadService logDownloadService;
	
	//첨부파일 다운로드
	@RequestMapping(value = "/cmp/download")
	public void fileDownload(@RequestParam(value = "cmpId", required = true) String cmpId,
			@RequestParam(value = "cmpType", required = true) String cmpType,
			 HttpServletRequest req, HttpServletResponse res) throws Exception {
		logger.debug("fileDownload");			
		
		DataMap param = HttpUtils.getServletRequestParam(req);

		logDownloadService.download(req, res, cmpId, cmpType);
	}
}
