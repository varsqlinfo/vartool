package com.vartool.web.app.handler.deploy;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vartool.web.app.config.ConfigProp;
import com.vartool.web.module.Utils;
import com.vartool.web.template.AntBuildTemplate;

public class SourceDeploy {
	private WatchKey watchKey;
	
	private Object lockObj = new Object();
	
	private final static Logger logger = LoggerFactory.getLogger(SourceDeploy.class);
	
	private long delayTime = ConfigProp.getInstance().getDelayTime();
	
	private String buildXmlPath = ConfigProp.getInstance().getBuildXmlPath();
	
	private String antFile = ConfigProp.getInstance().getAntFile(); 
	private String jarCopyFile = ConfigProp.getInstance().getJarCopyFile(); 
	
	private Kind<?>[] watchedEvents = { StandardWatchEventKinds.ENTRY_CREATE,
			StandardWatchEventKinds.ENTRY_DELETE,
			StandardWatchEventKinds.ENTRY_MODIFY };
	
	
	private static Thread autoDeployThread= null;
	private boolean stop=false;
	private boolean pause=false;
	private static String mode="start";
	
	public static void main(String[] args) {
		try {
			SourceDeploy sd = new SourceDeploy();
			sd.start();
			sd.pause();
			
			
			sd.resume();
			//sd.stop();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stop() {
		stop = true; 
	}
	
	public void pause() {
		mode="pause";
		pause = true; 
		logger.debug("pause");
	}
	
	public  void resume() {
		mode="start";
		pause = false;
		
		logger.debug("resume");
		
		synchronized (lockObj) {
			lockObj.notifyAll();
		}
	}
	
	public void start() throws IOException {
		stop = false; 
		mode="start";
		
		
		if(autoDeployThread != null) return ; 
		
		logger.debug("deploy start");
		
		//jarCopy();
		
		// watchService 생성
		WatchService watchService = FileSystems.getDefault().newWatchService();
		
		final String[] projectArr = ConfigProp.getInstance().getBuildProjects();
		final Map<String,String> projectInfoMap = new HashMap<String,String>();
		for(String projectName : projectArr){
			
			logger.info("projectName : "+projectName);
			
			String projectSrcPath = ConfigProp.getInstance().getSourcePath(projectName);
			
			projectInfoMap.put(projectSrcPath, projectName);
			
			logger.info("projectSrcPath : "+projectSrcPath);
			File file = new File(projectSrcPath); 
			
			if(file.exists() && file.isDirectory()){
				Path path = file.toPath();
				
				// register all subfolders
				Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult preVisitDirectory(Path dir,
							BasicFileAttributes attrs) throws IOException {

						dir.register(watchService,watchedEvents);
						return FileVisitResult.CONTINUE;
					}
				});
			}else{
				logger.info("diretory not found path: " +file.getAbsolutePath());
			}
		}
		Set<String> srcPathSet = projectInfoMap.keySet();
		
		try {
			if("Y".equals(ConfigProp.getInstance().getStartBuild())){
				Set<String> buildProjectInfo = new HashSet<String>(Arrays.asList(ConfigProp.getInstance().getStartBuildProjects()));
				changeFileDeploy(buildProjectInfo);
			}
		} catch (Exception e) {
			logger.error("start build error : {}" , e.getMessage() , e);
		}
		
		final String[] projectPath = Arrays.copyOf(srcPathSet.toArray(), srcPathSet.size() , String[].class);
		final int projectPathLen = projectPath.length;
		Hashtable<String, Long> dupChk = new Hashtable<String, Long>();
		
		autoDeployThread = new Thread(() -> {
			
			while (!stop) {
				try {
					synchronized (lockObj) {
						if(pause) {
							lockObj.wait();
						}
					}
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					watchKey = watchService.take();// 이벤트가 오길 대기(Blocking)
					
				} catch (InterruptedException e) {
					logger.error("watchKey = watchService.take(); InterruptedException : {} " , e.getMessage() ,e);
				}
				
				try {
					Set<String> deployProjectInfo = new HashSet<String>();
							
					for (WatchEvent<?> event : watchKey.pollEvents()) {
						// 이벤트 종류
						Kind<?> kind = event.kind();
			
						Path directory = (Path) watchKey.watchable();
			
						Path fileName = (Path) event.context();
			
						File file = directory.resolve(fileName.getFileName()).toFile();
			
						String absolutePath = file.getAbsolutePath();
			
						//String fileWatchInfo = kind +" : " + absolutePath;
						String fileWatchInfo = absolutePath;
						
						if (dupChk.containsKey(fileWatchInfo)) {
							if((dupChk.get(fileWatchInfo)+delayTime) < System.currentTimeMillis()) {
								dupChk.remove(fileWatchInfo);
							}
						}
						
						if(!dupChk.containsKey(fileWatchInfo)) {
							if (kind.equals(StandardWatchEventKinds.ENTRY_DELETE)
									|| kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)
									|| kind.equals(StandardWatchEventKinds.ENTRY_CREATE)
									) {
								
								String projectPathVal = "";
								String projectInfo = "";
								
								absolutePath = absolutePath.replaceAll("\\\\", "/");
								for(int i =0 ;i <projectPathLen; i++){
									projectPathVal = projectPath[i];
									if(absolutePath.indexOf(projectPathVal) > -1){
										projectInfo = projectInfoMap.get(projectPathVal);
										deployProjectInfo.add(projectInfo);
										break; 
									}
								}
								logger.info("change file info : "+kind+" ;; " +projectInfo +" :: "+fileWatchInfo);
								
							}
							dupChk.put(fileWatchInfo , System.currentTimeMillis());
						}
					}
					
					if (!watchKey.reset()) {
						try {
							watchService.close();
						} catch (IOException e) {
							logger.error("watchService.close() IOException : " +e.getMessage());
						}
					}
					
					if(deployProjectInfo.size() > 0){
						changeFileDeploy(deployProjectInfo);
					}
					
					deployProjectInfo = new HashSet<String>();
				}catch(Exception e) {
					logger.error("watchService Exception : {}" , e.getMessage() ,e);
				}
			}
		});
		
		autoDeployThread.start();
		
		logger.info("자동 배포 시작");

	}
	
	/**
	 * jar copy 
	 */
	private void jarCopy() {
		try {
			CommandLine cmdLine = new CommandLine(jarCopyFile);
			Executor executor = new DefaultExecutor();
			executor.setExitValue(0);
			executor.execute(cmdLine);
		} catch (Exception e) {
			logger.error("jarCopy {} ", e.getMessage(),e);
			e.printStackTrace();
		}
	}

	private void changeFileDeploy(Set<String> deployProjectInfo){
		
		String xmlFileName = buildXmlPath+Utils.UUID() +".xml";
		
		try {
			Map param = new HashMap();
			
			Map projectInfo;
			List projects = new ArrayList();
			
			for(String projectName : deployProjectInfo){
				projectInfo = new HashMap(); 
				
				projectInfo.put("prjectName", projectName);
				projectInfo.put("srcPath", ConfigProp.getInstance().getSourcePath(projectName));
				projectInfo.put("deployPath",ConfigProp.getInstance().getDeployPath(projectName));
				projectInfo.put("libPath", ConfigProp.getInstance().getJarDeployPath(projectName));
				
				
				projects.add(projectInfo);
			}
			
			param.put("projects", projects);
			
			FileUtils.writeStringToFile(new File(xmlFileName), AntBuildTemplate.getBuildXml(param));
			
			CommandLine cmdLine = new CommandLine(antFile);
			cmdLine.addArgument("-buildfile");
			//cmdLine.addArgument("C:/SDF-SDS/6.sourceGen/buildxml/build.xml");
			
			cmdLine.addArgument(xmlFileName);
			
			Executor executor = new DefaultExecutor();
			
			executor.setExitValue(0);
			
			executor.execute(cmdLine);
			
		} catch (Exception e) {
			logger.error("buildXmlPath generate error {}", e.getMessage(),e);
		}
		
		try{
			new File(xmlFileName).delete();
		}catch(Exception e){
			logger.error("build xml delete error : " +e.getMessage());
		}
	}
}
