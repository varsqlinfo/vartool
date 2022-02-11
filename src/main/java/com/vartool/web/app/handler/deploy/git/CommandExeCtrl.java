package com.vartool.web.app.handler.deploy.git;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vartool.web.app.handler.deploy.AbstractDeploy;
import com.vartool.web.dto.websocket.LogMessageDTO;

public class CommandExeCtrl {
	private final static Logger logger = LoggerFactory.getLogger(CommandExeCtrl.class);

	private AbstractDeploy deployAbstract;

	private String recvId;
	
	private LogMessageDTO logMessageDTO; 

	public CommandExeCtrl(AbstractDeploy deployAbstract, String recvId, LogMessageDTO logMessageDTO) {
		this.deployAbstract = deployAbstract;
		this.recvId = recvId;
		this.logMessageDTO = logMessageDTO;
	}

	public static void main(String[] args) throws Exception {
		CommandExeCtrl execTest = new CommandExeCtrl(null, null, null);

		CommandLine cmdLine = new CommandLine("E:/99.zzz.yona/apache-ant-1.10.2/bin/ant.bat");

		cmdLine.addArgument("-buildfile");
		cmdLine.addArgument("E:/03.sources/zzGain/src/config/build.xml");

		execTest.execAndRtnResult(cmdLine);
	}

	public String execAndRtnResult(CommandLine command) throws Exception {

		String rtnStr = "";

		logger.info("GitAntExec command {}", command.toString());

		ExecuteWatchdog watchdog = new ExecuteWatchdog(60 * 1000);

		Executor executor = new DefaultExecutor();

		executor.setExitValue(0);

		executor.setWatchdog(watchdog);
		LogOutputStream outputStream = new LogOutputStream(20000) {
			@Override
			protected void processLine(final String line, final int level) {
				send(line);
			}
		};

		LogOutputStream errorStream = new LogOutputStream(40000) {
			@Override
			protected void processLine(final String line, final int level) {
				send(" error : " + line);
			}
		};

		PumpStreamHandler pumpStreamHandler = new PumpStreamHandler(outputStream, errorStream);
		executor.setStreamHandler(pumpStreamHandler);

		executor.execute(command);
		return rtnStr;

	}

	protected void send(String line) {
		if (deployAbstract != null) {
			logMessageDTO.setLog(line);
			deployAbstract.sendLogMessage(logMessageDTO, recvId);
		} else {
			System.out.println(line);
		}
		
	}

	public String deleteDirPath(String commandLine, String delDirPath) throws Exception {
		CommandLine cmdLine = new CommandLine(commandLine);
		cmdLine.addArgument(delDirPath);
		return execAndRtnResult( cmdLine);
	}

	public String execAndRtnResult(String recvId, String antExeFile, String outputBuildFile) throws Exception {
		CommandLine cmdLine = new CommandLine(antExeFile);
		cmdLine.addArgument("-buildfile");
		cmdLine.addArgument(outputBuildFile);
		return execAndRtnResult(cmdLine);

	}
}