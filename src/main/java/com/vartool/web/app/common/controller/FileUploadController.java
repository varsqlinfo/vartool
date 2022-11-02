package com.vartool.web.app.common.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.vartech.common.app.beans.ResponseResult;
import com.vartool.web.app.common.service.FileUploadService;
import com.vartool.web.constants.AppCode;
import com.vartool.web.model.entity.FileInfoEntity;

/**
 * file upload controller
* 
* @fileName	: FileUploadController.java
* @author	: ytkim
 */
@Controller
@RequestMapping({ "/file" })
public class FileUploadController extends AbstractController {
	private final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

	@Autowired
	private FileUploadService fileUploadService;
	
	/**
	 * upload
	 *
	 * @method : fileUpload
	 * @param div 	구분
	 * @param paramFileContId	file content id
	 * @param contGroupId	file content group  id
	 * @param mtfRequest	
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = { "/upload" }, method = { RequestMethod.POST })
	@ResponseBody
	public ResponseResult fileUpload(@RequestParam(name = "div", required = true) String div,
			@RequestParam(name = "fileContId", defaultValue = "") String paramFileContId,
			@RequestParam(name = "contGroupId", defaultValue = "") String contGroupId,
			MultipartHttpServletRequest mtfRequest) throws IOException {
		this.logger.debug("file upload!");
		ResponseResult result = new ResponseResult();
		List<FileInfoEntity> uploadFiles = fileUploadService.uploadFiles(mtfRequest, div, paramFileContId, contGroupId);
		if (uploadFiles.size() > 0) {
			result.setList(uploadFiles);
		} else {
			result.setResultCode(AppCode.ErrorCode.COMM_FILE_EMPTY);
			result.setMessage("select file");
		}
		return result;
	}
}
