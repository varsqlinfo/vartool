package com.vartool.web.app.common.controller;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.vartech.common.app.beans.ResponseResult;
import com.vartool.web.app.common.service.FileUploadService;
import com.vartool.web.constants.AppCode;
import com.vartool.web.constants.UploadFileType;
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
		List<FileInfoEntity> uploadFiles = fileUploadService.uploadFiles(mtfRequest, UploadFileType.getDivType(div), paramFileContId, contGroupId);
		if (uploadFiles.size() > 0) {
			result.setList(uploadFiles);
		} else {
			result.setResultCode(AppCode.ErrorCode.COMM_FILE_EMPTY);
			result.setMessage("select file");
		}
		return result;
	}
	
	
	/**
	 * image file upload 
	 * 
	 * @param div
	 * @param paramFileContId
	 * @param mtfRequest
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="/imageUpload", method=RequestMethod.POST)
	public @ResponseBody ResponseResult imgFileUpload(
			 MultipartHttpServletRequest mtfRequest
			) throws IOException {
		logger.debug("image file upload!");
		
		ResponseResult result = new ResponseResult();
		
		Iterator<String> fileNameIter = mtfRequest.getFileNames();
		FileInfoEntity uploadFile = null;
		while (fileNameIter.hasNext()) {
			String fileFieldName = fileNameIter.next();
			List<MultipartFile> files = mtfRequest.getFiles(fileFieldName);
			if (files.size() > 0) {
				uploadFile = fileUploadService.onlySaveFile(UploadFileType.BOARD_CONTENT_IMAGE, files.get(0), "", false);
			}
		}
		
		if(uploadFile != null) {
			String viewId = uploadFile.getFilePath().replaceFirst(UploadFileType.BOARD_CONTENT_IMAGE.getSavePathRoot(), "");
			viewId = viewId.replaceAll("/", "");
			
			uploadFile.setFileContId(viewId);
			result.setItemOne(uploadFile);
		}else {
			result.setResultCode(AppCode.ErrorCode.COMM_FILE_EMPTY);
			result.setMessage("select file");
		}
		
		return result;
	}
}
