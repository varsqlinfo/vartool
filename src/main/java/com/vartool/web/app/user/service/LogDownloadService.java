package com.vartool.web.app.user.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vartool.core.common.CommonUtils;
import com.vartool.web.app.handler.command.CommandCmpManager;
import com.vartool.web.app.handler.deploy.DeployCmpManager;
import com.vartool.web.constants.ComponentConstants;
import com.vartool.web.constants.ComponentConstants.TYPE;
import com.vartool.web.constants.VartoolConstants;
import com.vartool.web.model.entity.cmp.CmpEntity;
import com.vartool.web.model.entity.cmp.CmpItemCommandEntity;
import com.vartool.web.model.entity.cmp.CmpItemLogEntity;
import com.vartool.web.repository.cmp.CmpItemCommandRepository;
import com.vartool.web.repository.cmp.CmpItemDeployRepository;
import com.vartool.web.repository.cmp.CmpItemLogRepository;

/**
 * log download service
* 
* @fileName	: LogDownloadService.java
* @author	: ytkim
 */
@Component
public class LogDownloadService {
	private final static Logger logger = LoggerFactory.getLogger(LogDownloadService.class);
	
	@Autowired
	private CmpItemLogRepository cmpItemLogRepository;
	
	@Autowired
	private CmpItemDeployRepository cmpItemDeployRepository;
	
	@Autowired
	private CmpItemCommandRepository cmpItemCommandRepository;
	
	/**
	 * log download
	 *
	 * @method : download
	 * @param req
	 * @param res
	 * @param cmpId
	 * @param type
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void download(HttpServletRequest req, HttpServletResponse res, String cmpId, String type) throws FileNotFoundException, IOException {
		
		ComponentConstants.TYPE cmpType = ComponentConstants.getComponentType(type);
		
		String downFileName= "";
		String filePath = "";
		String charset = VartoolConstants.CHAR_SET;
		
		CmpEntity entity = null; 
		if(ComponentConstants.TYPE.LOG.equals(cmpType)) {
			CmpItemLogEntity logEntity = cmpItemLogRepository.findByCmpId(cmpId);
			
			filePath = logEntity.getLogPath();
			entity = logEntity;
			charset = logEntity.getLogCharset();
			
		}else if(ComponentConstants.TYPE.DEPLOY.equals(cmpType)) {
			entity = cmpItemDeployRepository.findByCmpId(cmpId);
			
		}else if(ComponentConstants.TYPE.COMMAND.equals(cmpType)) {
			CmpItemCommandEntity cmdEntity = cmpItemCommandRepository.findByCmpId(cmpId);
			charset = cmdEntity.getCmdCharset();
			entity = cmdEntity;
		}
		
		if(entity == null) {
			throw new FileNotFoundException("id : "+ cmpId+" type : " +cmpType);
		}
		
		downFileName = entity.getName()+".log";
		
		res.setContentType("application/download; "+charset);
		res.setHeader("Content-Type", "application/octet-stream;");
		res.setHeader("Content-Transfer-Encoding", "binary");
		res.setHeader("Content-Disposition", "attachment;fileName="+CommonUtils.getDownloadFileName(req, downFileName)+ ";");
		
		if(ComponentConstants.TYPE.LOG.equals(cmpType)) {
			logDownload(res, cmpType, filePath);
			
			return ; 
		}else {
			String downText ="";
			if(ComponentConstants.TYPE.DEPLOY.equals(cmpType)) {
				downText = DeployCmpManager.getInstance().getLogContent(cmpId);
			}else if(ComponentConstants.TYPE.COMMAND.equals(cmpType)) {
				downText = CommandCmpManager.getInstance().getLogContent(cmpId);
			}
			
			try (ServletOutputStream output = res.getOutputStream();
					BufferedWriter out = new BufferedWriter(new OutputStreamWriter(output, charset))) {
				out.write(downText);
				out.close();
			}
		}
	}


	private void logDownload(HttpServletResponse res, TYPE cmpType, String filePath) throws FileNotFoundException, IOException {
		int bufferSize = 2048;
		
		File file = new File(filePath);
		
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
