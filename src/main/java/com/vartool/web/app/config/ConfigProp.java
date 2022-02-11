/**
(#)ConfigProp.java

Copyright (c) 2013 ytkim
All rights reserved.

CLASS_NAME : ConfigProp
프로그램 생성정보 :  2013-02-15 / ytkim
프로그램 수정정보 :  
*/

package com.vartool.web.app.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigProp{
	
	private final static Logger logger = LoggerFactory.getLogger(ConfigProp.class);
	
	protected static Object lock = new Object();
	
	private static File out_file = null;
	protected static Properties props = null;
	
	public final static String jdf_file_directory = "config/";
	private final static String jdf_file_name = "fileBuildConfig.properties";
	
	private ConfigProp(){
		super();
		try {
			props = new Properties();
			initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static class EpConfigHolder{
        private static final ConfigProp instance = new ConfigProp();
    }
	
	public static ConfigProp getInstance() {
		return EpConfigHolder.instance;
    }
	
	protected void initialize() throws Exception {
		synchronized(lock){	
			try{
				File currentConfigPath = new File(jdf_file_directory,jdf_file_name);
				
				File jdf_file = null;
				boolean firstSaveFlag = false;
				
				InputStream is = null;
				if(currentConfigPath.canRead()){
					jdf_file = currentConfigPath;
					is =new FileInputStream(jdf_file); 
				}else{
					firstSaveFlag = true; 
					is = ConfigProp.class.getClassLoader().getResourceAsStream(jdf_file_directory+jdf_file_name);
					
					if (is ==null){
						throw new Exception( this.getClass().getName() + " - Can't open jdf configuration file path: [" + jdf_file_directory+jdf_file_name +"]");
					}
				}
				
				out_file =jdf_file;
				props.load(new java.io.BufferedInputStream(is));
				
				if(firstSaveFlag==true){
					File parentDir = new File(currentConfigPath.getParent());
					
					if(!parentDir.exists()){
						parentDir.mkdir();
					}
					
					out_file =currentConfigPath;
					store();
				}
				
				is.close();
			}catch(Exception e){
				logger.error("ConfigProp : ",e);
				throw new Exception( this.getClass().getName() + e.getLocalizedMessage()+"\n"+ e.getMessage());
			}
		} // end of sunchronized(lock);
	}
	
	public Properties getProperties() {
		return props;
	}
	
	public String[] getBuildProjects(){
		return getSplitInfo("build.project",",");
	}
	
	public String getBuildXmlPath(){
		return props.getProperty("buildxml.path");
	}
	
	public String getAntFile(){
		return props.getProperty("ant.file");
	}
	
	public String getJarCopyFile(){
		return props.getProperty("jarcopy.file");
	}
	/**
	 * source path
	 * @param projectName
	 * @return
	 */
	public String getSourcePath(String projectName){
		if(projectName==null) return "";
		return props.getProperty(projectName.trim()+".src.path").replaceAll("\\\\", "/");
	}
	
	/**
	 * build path 
	 * @param projectName
	 * @return
	 */
	public String getDeployPath(String projectName){
		if(projectName==null) return "";
		return props.getProperty(projectName.trim()+".deploy.path").replaceAll("\\\\", "/");
	}
	
	public String getJarDeployPath(String projectName){
		if(projectName==null) return "";
		String val = props.getProperty(projectName.trim()+".lib.path");
		return (val==null?"":val).replaceAll("\\\\", "/");
	}
	
	
	/**
	 * 체크 시간. 
	 * @return
	 */
	public long getDelayTime(){
		String delayTime = props.getProperty("delay.time");
		try{
			return Long.parseLong(delayTime);
		}catch(NumberFormatException e){
			
		}
		
		return 1000;
	}
	
	public void refresh(){
		try {
			initialize();
		} catch (Exception e) {
			logger.error("ConfigProp refresh : ",e);
			e.printStackTrace();
		}
	}
	
	public void store() throws FileNotFoundException, IOException{
		props.store(new OutputStreamWriter(new FileOutputStream(out_file)),"fileBuildConfig");
	}

	public String getStartBuild() {
		return props.getProperty("start.build");
	}
	
	public String[] getStartBuildProjects(){
		return getSplitInfo("start.build.project",",");
	}

	private String[] getSplitInfo(String key, String delimiter) {
		
		String val = props.getProperty(key);
		
		String [] splitArr = val.split(","); 
		
		String [] reval = new String[splitArr.length];
		
		int idx = -1; 
		for (String tmpVal : splitArr) {
			reval[++idx] = tmpVal.trim();
		}
		return reval;
	}
}
