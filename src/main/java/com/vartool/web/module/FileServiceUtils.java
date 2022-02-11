package com.vartool.web.module;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import com.vartech.common.exception.VartechRuntimeException;
import com.vartech.common.utils.FileUtils;
import com.vartool.web.constants.VartoolConstants;
import com.vartool.web.model.entity.FileBaseEntity;
import com.vartool.web.model.entity.FileInfoEntity;
import com.vartool.web.model.entity.board.BoardFileEntity;

/**
*-----------------------------------------------------------------------------
* @PROJECT	: varsql
* @NAME		: FileServiceUtils.java
* @DESC		: file service utils
* @AUTHOR	: ytkim
*-----------------------------------------------------------------------------
  DATE			AUTHOR			DESCRIPTION
*-----------------------------------------------------------------------------
*2018. 7. 24. 			ytkim			최초작성

*-----------------------------------------------------------------------------
 */
public final class FileServiceUtils {

	private FileServiceUtils() {};


	/**
	 * @method  : getUploadRootPath
	 * @desc : get upload root path
	 * @author   : ytkim
	 * @date   : 2021. 1. 3.
	 * @return
	 */
	public static Path getUploadRootPath(){
		return Paths.get(VartoolConstants.UPLOAD_PATH).toAbsolutePath().normalize();
	}

	/**
	 *
	 * @Method Name  : getUploadFile
	 * @Method 설명 : upload file 얻기
	 * @작성자   : ytkim
	 * @작성일   : 2019. 10. 31.
	 * @변경이력  :
	 * @param url
	 * @return
	 */
	public static File getUploadFile(FileBaseEntity fileinfo){
		Path filePath = getUploadRootPath().resolve(fileinfo.getFilePath()).normalize();
		return filePath.toFile();
	}
	
	public static File getUploadFile(BoardFileEntity fileinfo){
		Path filePath = getUploadRootPath().resolve(fileinfo.getFilePath()).normalize();
		return filePath.toFile();
	}

	/**
	 * @method  : getSavePath
	 * @desc :
	 * @author   : ytkim
	 * @date   : 2021. 1. 3.
	 * @param div
	 * @return
	 * @throws IOException
	 */
	public static Path getSavePath(String div) throws IOException {
		Path fileExportPath = getUploadRootPath().resolve(FileUtils.getSaveDayPath(div));

		if (Files.notExists(fileExportPath)) {
			Files.createDirectories(fileExportPath);
		}
		return fileExportPath;
	}

	/**
	 * @method  : getSaveRelativePath
	 * @desc : save
	 * @author   : ytkim
	 * @date   : 2021. 1. 3.
	 * @param div
	 * @return
	 * @throws IOException
	 */
	public static String getSaveRelativePath(String div) {

		String relativePath = FileUtils.getSaveMonthPath(div);
		Path fileExportPath = getUploadRootPath().resolve(relativePath);

		if (Files.notExists(fileExportPath)) {
			try {
				Files.createDirectories(fileExportPath);
			} catch (IOException e) {
				throw new VartechRuntimeException(e.getMessage() , e);
			}
		}
		return relativePath;
	}

	/**
	 * @method  : loadFileAsResource
	 * @desc : file nam
	 * @author   : ytkim
	 * @date   : 2021. 1. 3.
	 * @param fileName
	 * @return
	 * @throws MalformedURLException
	 * @throws FileNotFoundException 
	 */
	public static Resource loadFileAsResource(String filePath) throws MalformedURLException, FileNotFoundException {
		Path path = getUploadRootPath().resolve(filePath).normalize();

		Resource resource = new UrlResource(path.toUri());
		if (resource.exists()) {
			return resource;
		} else {
			throw new FileNotFoundException("File resource not found " + filePath);
		}
	}

	/**
	 * @method  : resolve
	 * @desc :
	 * @author   : ytkim
	 * @date   : 2021. 1. 3.
	 * @param filePath
	 * @return
	 */
	public static Path getPath(String filePath) {
		return getUploadRootPath().resolve(filePath).normalize();
	}
	
	/**
	 * 
	 * @Method Name  : byteCalculation
	 * @Method 설명 : size 계산 
	 * @작성자   : ytkim
	 * @작성일   : 2021. 7. 2.
	 * @변경이력  :
	 * @param size
	 * @return
	 */
	public static String byteCalculation(long size) {
		String retFormat = "0";
		
		String[] s = { "bytes", "KB", "MB", "GB", "TB", "PB" };

		if (size > 0) {
			int idx = (int) Math.floor(Math.log(size) / Math.log(1024));
			DecimalFormat df = new DecimalFormat("#,###.##");
			double ret = ((size / Math.pow(1024, Math.floor(idx))));
			retFormat = df.format(ret) + " " + s[idx];
		} else {
			retFormat += " " + s[0];
		}

		return retFormat;
	}
	
	public static void fileDownload(HttpServletRequest req, HttpServletResponse res, String downFileName, FileBaseEntity ... fileArr) throws IOException {
		res.setContentType("application/download; "+VartoolConstants.CHAR_SET);
		res.setHeader("Content-Type", "application/octet-stream;");
		res.setHeader("Content-Transfer-Encoding", "binary");
		res.setHeader("Content-Disposition", "attachment;fileName="+CommonUtils.getDownloadFileName(req, downFileName)+ ";");
		
		int bufferSize = 2048;
		int fileLen = fileArr.length;
		
		if(fileLen > 1) {
			try(ZipOutputStream zos = new ZipOutputStream(res.getOutputStream(), Charset.forName(VartoolConstants.CHAR_SET));){
				for(int idx=0; idx < fileLen; idx++){
					FileBaseEntity fileInfo = fileArr[idx];	
					
					File file = FileServiceUtils.getUploadFile(fileInfo);
					
					if (file.isFile()) {
						
						String orginName = fileInfo.getFileName();
						
						try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))){
							ZipEntry zentry = new ZipEntry(orginName);
					        zos.putNextEntry(zentry);
					        
					        byte[] buffer = new byte[bufferSize];
					        int cnt = 0;
					        while ((cnt = bis.read(buffer, 0, bufferSize)) != -1) {
					            zos.write(buffer, 0, cnt);
					        }
					        zos.closeEntry();
						};
					}
				}
				if(zos != null) zos.close();
			}
		}else {
			File file = FileServiceUtils.getUploadFile(fileArr[0]);
			
			byte b[] = new byte[bufferSize];

			if (file.isFile()) {
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
}
