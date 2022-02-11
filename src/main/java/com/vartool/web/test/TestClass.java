package com.vartool.web.test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vartool.web.model.entity.board.BoardEntity;

public class TestClass {
	public static void main(String[] args) {
		String aaa = "C:\\00.vsql_deploy\\tomcat\\tomcat-8.5.50-deploy\\logs\\varsql\\varsql.log"; 
		
		BoardEntity entity= BoardEntity.builder().authorName("test").build();
		entity.setRegDt(LocalDateTime.now());
		entity.setUpdDt(LocalDateTime.now());
		
		try {
			System.out.println(new ObjectMapper().writeValueAsString(entity));
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
		if(true) return ; 
		System.out.println(System.getenv("JAVA_HOME"));
		
		try {
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static void commandExecute(String command) throws ExecuteException, IOException {
		System.out.println(command);
		
		String exeFile = "c:/zzz/aaa.bat";
		
		FileUtils.write(new File(exeFile), command);
		
		PumpStreamHandler streamHandler = new PumpStreamHandler(
				new PritingLogOutputStream());
		
		CommandLine cmdLine =  new CommandLine(exeFile);
		
		//CommandLine cmdLine = new CommandLine(command);
		Executor executor = new DefaultExecutor();
		executor.setStreamHandler(streamHandler);
		executor.setExitValue(0);
		executor.execute(cmdLine);
		
	}
	
	private static class PritingLogOutputStream extends LogOutputStream {

		private StringBuilder output = new StringBuilder();

		@Override
		protected void processLine(String line, int level) {
			System.out.println("line : "+ line);
			

			output.append(line).append("\n");
		}

		public String getOutput() {
			return output.toString();
		}
	}
}
