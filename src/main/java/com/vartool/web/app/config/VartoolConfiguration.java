package com.vartool.web.app.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.vartech.common.utils.DateUtils;
import com.vartool.web.app.config.wrapper.DeployConfig;
import com.vartool.web.app.config.wrapper.VartoolConfig;
import com.vartool.web.constants.VartoolConstants;
import com.vartool.web.exception.ConfigurationLoadException;
import com.vartool.web.module.CommonUtils;
import com.vartool.web.module.ResourceUtils;



/**
 * 
 * @FileName : VartoolConfigFactory.java
 * @Author   : ytkim
 * @Program desc : config
 * @Hisotry :
 */
public class VartoolConfiguration extends AbstractConfiguration {
	
	private final static Logger logger = LoggerFactory.getLogger(VartoolConfiguration.class);
	
	private Object lock = new Object();
	
	private static String CONFIG_ROOT_PATH = getConfigRootPath();
	
	private enum CONFIG_FILE {
		VARTOOL_CONFIG("config/vartool-config.xml", new TypeReference<VartoolConfig> () {});

		private TypeReference beanClass;

		private String filePath;
		
		private String configFilePath;
		
		CONFIG_FILE(String fileName, TypeReference class1) {
			this.filePath = fileName;
			this.beanClass = class1;
			configFilePath = String.format("%s/%s", CONFIG_ROOT_PATH, fileName) ;
		}

		public String getFilePath() {
			return this.filePath;
		}

		public TypeReference getBeanClass() {
			return this.beanClass;
		}

		public String getConfigFilePath() {
			return configFilePath;
		}
	}
	
	private XmlMapper xmlMapper;
	
	private VartoolConfig vartoolConfig;
	
	public static VartoolConfiguration getInstance() {
		return VartoolConfigHolder.instance;
	}
	
	private static class VartoolConfigHolder {
		private static final VartoolConfiguration instance = new VartoolConfiguration();
	}

	private VartoolConfiguration() {
		init();
	}
	
	private void init() {

		logger.info("config root path : {}", CONFIG_ROOT_PATH);
		try {
			JacksonXmlModule module = new JacksonXmlModule();
			module.setDefaultUseWrapper(false);
			xmlMapper = new XmlMapper(module);
			xmlMapper.enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION);
			xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
			
			for (CONFIG_FILE configInfo : CONFIG_FILE.values()) {
				
				logger.info("config file : {}", configInfo.getFilePath());
				boolean readFlag = true; 
				Resource resources = ResourceUtils.getResource(configInfo.getConfigFilePath());
				if (resources == null || !resources.exists()) {
					resources =  ResourceUtils.getResource(ResourceUtils.CLASS_PREFIX+configInfo.getFilePath());
					
					if (resources == null || !resources.exists()) {
						readFlag =false; 
					}else {
						FileUtils.copyInputStreamToFile(resources.getInputStream(), new File(configInfo.getConfigFilePath()));
					}
				}
				
				loadXml(configInfo, readFlag);
			}
			
			
		} catch (IOException | ConfigurationLoadException e) {
			logger.error("vartool config info", e);
		}
		
		logger.debug("vartool config info");
		logger.debug("os type : {}", vartoolConfig.getOsType());
		logger.debug("java home  : {}", vartoolConfig.getJavaHome());
		logger.debug("vartool config info");
	}

	public static void main(String[] args) {
		VartoolConfiguration udc = getInstance();
		
		try {
			//System.out.println(VartechReflectionUtils.reflectionToString(udc.deployItemWrapper));
			System.out.println("--------------");
			System.out.println(udc.getDeployConfig());
			System.out.println("--------------");
			//System.out.println(udc.serverItemWrapper.getServerItems().get(0).getLogPathList());
			
			//System.out.println(VartechReflectionUtils.reflectionToString(udc.vartoolConfig));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadXml(CONFIG_FILE configInfo, boolean readFlag) {
		synchronized(lock) {
			String xml=null;
			try {
				xml = ResourceUtils.getResourceString(ResourceUtils.getResource(configInfo.getConfigFilePath()));
				
				if(configInfo == CONFIG_FILE.VARTOOL_CONFIG) {
					loadMainConfig(xml, configInfo);
				}
				
			} catch (IOException e) {
				logger.error("error xml : {}", xml);
				logger.error("vartool config info", e);
				
				try {
					FileUtils.write(new File(configInfo.getConfigFilePath()+"."+DateUtils.getCurrentDate()), xml, VartoolConstants.CHAR_SET);
				} catch (IOException e1) {
					logger.error("backup xml path:{} , error message: {}", configInfo.getConfigFilePath(), e1.getMessage());
				}
			}
		}
	}
	private void loadMainConfig(String xml, CONFIG_FILE configInfo) throws JsonProcessingException {
		
		try {
			vartoolConfig = (VartoolConfig) xmlMapper.readValue(xml, configInfo.getBeanClass());
		} catch (JsonProcessingException e) {
			logger.error("error xml : {}", xml, e.getMessage(), e);
			vartoolConfig = new VartoolConfig();
		}
		
		if(StringUtils.isBlank(vartoolConfig.getJavaHome())) {
			vartoolConfig.setJavaHome(CommonUtils.getJavaHome());
		}
		
		if(StringUtils.isBlank(vartoolConfig.getOsType())) {
			vartoolConfig.setOsType(CommonUtils.getOsName());
		}
		
		if(StringUtils.isBlank(vartoolConfig.getCharset())) {
			vartoolConfig.setCharset(VartoolConstants.CHAR_SET);
		}
		
		if(StringUtils.isBlank(vartoolConfig.getCommandSavePath())) {
			String defaultCmdPath = CONFIG_ROOT_PATH +File.separator+"vtoolCmd"; 
			if(!new File(defaultCmdPath).exists()) {
				new File(defaultCmdPath).mkdir();
			}
			vartoolConfig.setCommandSavePath(defaultCmdPath);
		}
		
		if(StringUtils.isBlank(vartoolConfig.getUploadPath())) {
			String defaultPath = CONFIG_ROOT_PATH +File.separator+"upload"; 
			if(!new File(defaultPath).exists()) {
				new File(defaultPath).mkdir();
			}
			vartoolConfig.setUploadPath(defaultPath);
		}
		
		if(vartoolConfig.getFileUploadMaxInMemorySize() < 0) {
			vartoolConfig.setFileUploadMaxInMemorySize(0);
		}
		
		DeployConfig deployConfig = vartoolConfig.getDeployConfig();
		if(deployConfig == null) {
			deployConfig = new DeployConfig();
		}
		
		if(StringUtils.isBlank(deployConfig.getSourcePath())) {
			String sourcePath = CONFIG_ROOT_PATH +File.separator+"deploy"; 
			if(!new File(sourcePath).exists()) {
				new File(sourcePath).mkdir();
			}
			
			deployConfig.setSourcePath(sourcePath);
		}
		
		if(StringUtils.isBlank(deployConfig.getBuildFileCreatePath())) {
			String buildPath = CONFIG_ROOT_PATH +File.separator+"buidFile"; 
			if(!new File(buildPath).exists()) {
				new File(buildPath).mkdir();
			}
			
			deployConfig.setBuildFileCreatePath(buildPath);
		}
		
		vartoolConfig.setDeployConfig(deployConfig);	
		
		logger.info("tool config info : {}", vartoolConfig);
	}

	
	public VartoolConfig getConfigInfo() {
		return vartoolConfig;
	}
	
	public DeployConfig getDeployConfig() {
		return vartoolConfig.getDeployConfig();
	}
	
	public String getDefaultCharset() {
		return vartoolConfig.getCharset();
	}
	
	private void store(CONFIG_FILE configInfo) {
		
		Object obj= null; 
		
		if(configInfo == CONFIG_FILE.VARTOOL_CONFIG) {
			obj = vartoolConfig;
		}
		
		try(FileOutputStream fos = new  FileOutputStream(configInfo.getConfigFilePath())) {
			xmlMapper.writeValue(fos, obj);
		} catch(Exception e) {
			logger.error("store error : {}", e.getMessage(), e);
		}
	}

	public long getFileUploadSize() {
		return vartoolConfig.getFileUploadSize();
	}

	public long getFileUploadSizePerFile() {
		return vartoolConfig.getFileUploadSizePerFile();
	}
	
	public int getFileUploadMaxInMemorySize() {
		return vartoolConfig.getFileUploadMaxInMemorySize();
	}
	
	public int getCommandSavePath() {
		return vartoolConfig.getFileUploadMaxInMemorySize();
	}
}
