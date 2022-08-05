package com.vartool.core.config;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.vartech.common.crypto.password.PasswordType;
import com.vartech.common.utils.StringUtils;
import com.vartech.common.utils.VartechReflectionUtils;
import com.vartool.core.config.vo.AppConfig;
import com.vartool.core.config.vo.DeployConfig;
import com.vartool.core.config.vo.MailConfig;
import com.vartool.web.constants.VartoolConstants;
import com.vartool.web.exception.ConfigurationLoadException;
import com.vartool.web.module.CommonUtils;
import com.vartool.web.module.ResourceUtils;


/**
 * vartool config
* 
* @fileName	: VartoolConfiguration.java
* @author	: ytkim
 */
public class VartoolConfiguration extends AbstractConfiguration {
	
	final public static String CONFIG_DIR_PATH = "config/";
	
	private final static Logger logger = LoggerFactory.getLogger(VartoolConfiguration.class);
	
	private final static String CHARSET = "utf-8";
	
	private Object lock = new Object();
	
	private static String CONFIG_ROOT_PATH = getConfigRootPath();
	
	private String APP_CONFIG_PATH  = CONFIG_DIR_PATH+"vartool-app-config.yaml";
	
	private String siteAddr; 
	
	private ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
	
	private AppConfig appConfig;
	
	private VartoolConstants.PASSWORD_RESET_MODE passwordResetMode;
	
	private PasswordType passwordType;

	private int passowrdSize;
	
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

		logger.info("config file path : {}, fileName : {}", CONFIG_ROOT_PATH , APP_CONFIG_PATH);
		try {
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			
			Resource resources = ResourceUtils.getResource(String.format("%s/%s", CONFIG_ROOT_PATH, APP_CONFIG_PATH) );
			
			if (resources == null || !resources.exists()) {
				resources =  ResourceUtils.getResource(ResourceUtils.CLASS_PREFIX+APP_CONFIG_PATH);
			}
		
			appConfig = mapper.readValue(resources.getFile(), AppConfig.class);
			
			loadMainConfig();
		} catch (IOException | ConfigurationLoadException e) {
			logger.error("vartool config info", e);
		}
		
		logger.debug("vartool config info");
		logger.debug("os type : {}", appConfig.getOsType());
		logger.debug("java home  : {}", appConfig.getJavaHome());
		logger.debug("vartool config info");
	}

	public static void main(String[] args) {
		VartoolConfiguration udc = getInstance();
		
		try {
			//System.out.println(VartechReflectionUtils.reflectionToString(udc.deployItemWrapper));
			System.out.println("--------------");
			System.out.println(VartechReflectionUtils.reflectionToString(udc.getDeployConfig()));
			System.out.println("--------------");
			//System.out.println(udc.serverItemWrapper.getServerItems().get(0).getLogPathList());
			
			//System.out.println(VartechReflectionUtils.reflectionToString(udc.vartoolConfig));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadMainConfig() throws JsonProcessingException {
		
		if(StringUtils.isBlank(appConfig.getJavaHome())) {
			appConfig.setJavaHome(CommonUtils.getJavaHome());
		}
		
		if(StringUtils.isBlank(appConfig.getOsType())) {
			appConfig.setOsType(CommonUtils.getOsName());
		}
		
		if(StringUtils.isBlank(appConfig.getCharset())) {
			appConfig.setCharset(CHARSET);
		}
		
		if(StringUtils.isBlank(appConfig.getCommandSavePath())) {
			String defaultCmdPath = CONFIG_ROOT_PATH +File.separator+"vtoolCmd"; 
			if(!new File(defaultCmdPath).exists()) {
				new File(defaultCmdPath).mkdir();
			}
			appConfig.setCommandSavePath(defaultCmdPath);
		}
		
		if(StringUtils.isBlank(appConfig.getUploadPath())) {
			String defaultPath = CONFIG_ROOT_PATH +File.separator+"upload"; 
			if(!new File(defaultPath).exists()) {
				new File(defaultPath).mkdir();
			}
			appConfig.setUploadPath(defaultPath);
		}
		
		if(appConfig.getFileUploadMaxInMemorySize() < 0) {
			appConfig.setFileUploadMaxInMemorySize(0);
		}
		
		this.passwordResetMode = VartoolConstants.PASSWORD_RESET_MODE.getMode(appConfig.getUserPassowrdResetConfig().getMode());
		this.passwordType = PasswordType.getType(appConfig.getUserPassowrdResetConfig().getType());
		this.passowrdSize = appConfig.getUserPassowrdResetConfig().getSize() < 8 ? 8 : appConfig.getUserPassowrdResetConfig().getSize();
		
		if(StringUtils.isBlank(appConfig.getDeploy().getSourcePath())) {
			String sourcePath = CONFIG_ROOT_PATH +File.separator+"deploy"; 
			if(!new File(sourcePath).exists()) {
				new File(sourcePath).mkdir();
			}
			
			appConfig.getDeploy().setSourcePath(sourcePath);
		}
		
		if(StringUtils.isBlank(appConfig.getDeploy().getBuildFileCreatePath())) {
			String buildPath = CONFIG_ROOT_PATH +File.separator+"buidFile"; 
			if(!new File(buildPath).exists()) {
				new File(buildPath).mkdir();
			}
			
			appConfig.getDeploy().setBuildFileCreatePath(buildPath);
		}
		
		if("localhost".equals(appConfig.getHostname())) {
			
			try {
				appConfig.setHostname(InetAddress.getLocalHost().getHostAddress());
			} catch (UnknownHostException e) {
				logger.error(e.getMessage(), e);
			}
		}
		
		if(appConfig.getMail() == null) {
			appConfig.setMail(MailConfig.builder().enableMail(false).build());
		}
		
		// service protocal http or https
		if(StringUtils.isBlank(appConfig.getProtocol())) {
			appConfig.setProtocol("http");
		}
		//service host name
		if(StringUtils.isBlank(appConfig.getHostname())) {
			appConfig.setHostname("localhost");
		}

		if(appConfig.getPort()  < 1) {
			appConfig.setPort(12346);
		}
		
		if(StringUtils.isBlank(appConfig.getContextPath())) {
			appConfig.setContextPath("/vtool");
		}
		
		String hostname = appConfig.getHostname();
		
		if("localhost".equals(hostname)) {
					
			try {
				hostname = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				logger.error(e.getMessage(), e);
			}
		}
		
		siteAddr = String.format("%s://%s:%d%s", appConfig.getProtocol(), hostname, appConfig.getPort(), appConfig.getContextPath());
		
		logger.debug("siteAddr : {}", siteAddr);
		logger.debug("tool config info : {}", appConfig);
	}

	
	public AppConfig getConfigInfo() {
		return appConfig;
	}
	
	public DeployConfig getDeployConfig() {
		return appConfig.getDeploy();
	}
	
	public MailConfig getMailConfig() {
		return appConfig.getMail();
	}
	
	public String getDefaultCharset() {
		return appConfig.getCharset();
	}
	
	public long getFileUploadSize() {
		return appConfig.getFileUploadSize();
	}

	public long getFileUploadSizePerFile() {
		return appConfig.getFileUploadSizePerFile();
	}
	
	public int getFileUploadMaxInMemorySize() {
		return appConfig.getFileUploadMaxInMemorySize();
	}
	
	public String getCommandSavePath() {
		return appConfig.getCommandSavePath();
	}
	
	public String getContextPath() {
		return appConfig.getContextPath();
	}

	public VartoolConstants.PASSWORD_RESET_MODE getPasswordResetMode() {
		return passwordResetMode;
	}
	
	public PasswordType passwordType (){
		return passwordType;
	}

	public int passwordInitSize (){
		return passowrdSize;
	}

	public String getSiteAddr() {
		return siteAddr;
	}

	/**
	 * password security key
	 * @return
	 */
	public String getPwSecurityKey() {
		return appConfig.getPasswordConfig().getSecretKey();
	}
	
	/**
	 * password crypto type
	 * @return
	 */
	public String getPWCryptoType() {
		return appConfig.getPasswordConfig().getCrpyto();
	}
	
	/**
	 * password encrypt, decrypt custom class
	 * @return
	 */
	public String getPWCustomClass() {
		return appConfig.getPasswordConfig().getCustomClass();
	}
	
	
}
