package com.vartool.web.app.common.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.vartech.common.exception.VartechRuntimeException;
import com.vartech.common.utils.FileUtils;
import com.vartech.common.utils.StringUtils;
import com.vartech.common.utils.VartechUtils;
import com.vartool.web.app.common.controller.FileUploadController;
import com.vartool.web.constants.FilePathCode;
import com.vartool.web.exception.FileUploadException;
import com.vartool.web.model.entity.FileInfoEntity;
import com.vartool.web.module.FileServiceUtils;
import com.vartool.web.repository.FileInfoRepository;

/**
*-----------------------------------------------------------------------------
* 
* @NAME		: FileUploadService.java
* @DESC		: file upload service
* @AUTHOR	: ytkim
*-----------------------------------------------------------------------------
  DATE			AUTHOR			DESCRIPTION
*-----------------------------------------------------------------------------
*2018. 7. 24. 			ytkim			최초작성

*-----------------------------------------------------------------------------
 */
@Service
public class FileUploadService {

	private final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

	@Autowired
	private FileInfoRepository fileInfoEntityRepository;

	/**
	 *
	 * @Method Name  : uploadFile
	 * @Method 설명 : 파일 저장.
	 * @작성자   : ytkim
	 * @작성일   : 2019. 10. 31.
	 * @변경이력  :
	 * @param url
	 * @return
	 */

	public FileInfoEntity uploadFile(MultipartHttpServletRequest mtfRequest, String div, String paramFileContId) {
		List<FileInfoEntity> uploadFiles = uploadFiles(mtfRequest, div, paramFileContId);

		if(uploadFiles.size() > 0) {
			return uploadFiles.get(0);
		}else {
			return null;
		}
	}

	/**
	 *
	 * @Method Name  : uploadFiles
	 * @Method 설명 : 멀티 파일 저장.
	 * @작성자   : ytkim
	 * @작성일   : 2019. 10. 31.
	 * @변경이력  :
	 * @param url
	 * @return
	 */
	public List<FileInfoEntity> uploadFiles(MultipartHttpServletRequest mtfRequest, String div, String paramFileContId) {
		return uploadFiles(mtfRequest, div, paramFileContId, null);
	}

	public List<FileInfoEntity> uploadFiles(MultipartHttpServletRequest mtfRequest, String div, String paramFileContId,	String contGroupId) {
		final String fileContId =StringUtils.isBlank(paramFileContId) ? VartechUtils.generateUUID() :paramFileContId;

		Iterator<String> fileNameIter = mtfRequest.getFileNames();

		List<FileInfoEntity> uploadFiles = new ArrayList<FileInfoEntity>();
		
		FilePathCode divInfo = FilePathCode.getFileType(div);
		// 파일 날짜별(yyyyMMdd) 로 생성 하기
        // 업무 구분 + 날짜별 디렉토리 위치

		while(fileNameIter.hasNext()) {
    		String fileFieldName  = fileNameIter.next();

    		List<MultipartFile> files = mtfRequest.getFiles(fileFieldName);

    		uploadFiles.addAll(uploadFiles(files, divInfo.getDiv(), fileContId, contGroupId, fileFieldName, false));
    	}

		if(divInfo.isDbSave() && uploadFiles.size() > 0 ) {
			fileInfoEntityRepository.saveAll(uploadFiles);
		}

		return uploadFiles;
	}
	
	public List<FileInfoEntity> uploadFiles(List<MultipartFile> files, String div
			, String fileContId, String contGroupId, String fileFieldName, boolean saveFlag) {
		String filePath = FileServiceUtils.getSaveRelativePath(div);
		
		List<FileInfoEntity> fileInfos = new ArrayList<FileInfoEntity>();
		files.forEach(file ->{
			if(!file.isEmpty()) {
				try {
					FileInfoEntity fileInfo = saveFile(div, file, filePath);
					fileInfo.setFileContId(fileContId);
					fileInfo.setFileFieldName(fileFieldName);
					fileInfo.setContGroupId(contGroupId);
					fileInfos.add(fileInfo);
				} catch (IllegalStateException | IOException e) {
					logger.error("file upload exception : {}", e.getMessage(), e);
					throw new VartechRuntimeException("file upload error",  e);
				}
			}
		});
		
		if(saveFlag) {
			fileInfoEntityRepository.saveAll(fileInfos);
		}
		
		return fileInfos; 
	}

	/**
	 *
	 * @Method Name  : saveFile
	 * @Method 설명 : 파일 저장.
	 * @작성자   : ytkim
	 * @작성일   : 2019. 10. 31.
	 * @변경이력  :
	 * @param url
	 * @return
	 */
	private FileInfoEntity saveFile(String div, MultipartFile mfileInfo, String filePath) throws IllegalStateException, IOException {
		// 파일 존재 확인
		if (mfileInfo.isEmpty()) {
			throw new FileUploadException("File empty : " + mfileInfo.getOriginalFilename());
		}

		// 파일 원본명
		String fileName = FileUtils.normalize(mfileInfo.getOriginalFilename());

		// 파일 확장자 구하기
		String extension = FileUtils.extension(fileName);

		String fileId = VartechUtils.generateUUID();

		filePath = FileUtils.pathConcat(filePath, fileId);

		Path createFilePath = FileServiceUtils.getPath(filePath);

		mfileInfo.transferTo(createFilePath);

		return FileInfoEntity.builder()
				.fileDiv(div)
				.fileId(fileId)
				.fileName(fileName)
				.fileSize(mfileInfo.getSize())
				.fileExt(extension)
				.filePath(filePath).build();

	}
}