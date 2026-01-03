package com.vartool.web.app.common.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vartool.web.constants.UploadFileType;
import com.vartool.web.model.entity.FileBaseEntity;
import com.vartool.web.module.FileServiceUtils;



/**
 *
*-----------------------------------------------------------------------------
* @PROJECT	: varsql
* @NAME		: DownloadController.java
* @DESC		: 공통 다운로드 처리.
* @AUTHOR	: ytkim
*-----------------------------------------------------------------------------
  DATE			AUTHOR			DESCRIPTION
*-----------------------------------------------------------------------------
* 2019. 4. 9. 			ytkim			최초작성

*-----------------------------------------------------------------------------
 */
@Controller
public class DownloadController extends AbstractController {

	/** The Constant logger. */
	private final Logger logger = LoggerFactory.getLogger(DownloadController.class);


	@RequestMapping("/imageView/{fileId}")
    public void imgView(@PathVariable("fileId") String fileId 
    		, @RequestParam(name = "div", defaultValue = "boardContent") String div
    		, HttpServletRequest req ,HttpServletResponse res) throws FileNotFoundException, java.io.FileNotFoundException, IOException {
    	
		if(logger.isDebugEnabled()) {
			logger.debug("imageView div: {}, fileId :{}", div,fileId);
		}
		
		String yyyy = fileId.substring(0, 4);
		String mm = fileId.substring(4, 6);
		String id = fileId.substring(6);
		
		String path = String.format("%s/%s/%s/%s", UploadFileType.BOARD_CONTENT_IMAGE.getSavePathRoot(), yyyy, mm, id) ;
		
		FileBaseEntity fbe = new FileBaseEntity();
		
		fbe.setFilePath(path);
		
		File file = FileServiceUtils.getFileInfoToFile(fbe);
		
		if (file.isFile()) {
			byte b[] = new byte[FileServiceUtils.BUFFER_SIZE];
			
			try(BufferedInputStream fin = new BufferedInputStream(new FileInputStream(file));
				BufferedOutputStream outs = new BufferedOutputStream(res.getOutputStream());){
				int read = 0;
				while ((read = fin.read(b)) != -1){
					outs.write(b,0,read);
				}

				if(fin != null) fin.close();
				if(outs != null) outs.close();
			}
		}
    }
}
