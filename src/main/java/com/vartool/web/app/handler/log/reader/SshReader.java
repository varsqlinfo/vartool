package com.vartool.web.app.handler.log.reader;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.SshException;
import org.apache.sshd.common.future.WaitableFuture;
import org.apache.sshd.common.util.io.output.NoCloseOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vartech.common.utils.StringUtils;
import com.vartool.core.crypto.PasswordCryptionFactory;
import com.vartool.web.app.handler.log.stream.LogComponentOutputStream;
import com.vartool.web.dto.ReadLogInfo;

/**
 * ssh read
* 
* @fileName	: SshReader.java
* @author	: ytkim
 */
public class SshReader implements LogReader{
	private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private Charset logCharset = Charset.defaultCharset();

    private SshConnectionInfo conn;
	private String cmd;
	private long timeout;
	private SshClient client;
	private LogComponentOutputStream output;
	private boolean stopped = false;
	
	public SshReader(ReadLogInfo readLogInfo) {
		
		SshConnectionInfo conn = SshConnectionInfo.builder()
				.hostname(readLogInfo.getRemoteHost())
				.port(readLogInfo.getRemotePort())
				.username(readLogInfo.getCredentialInfo().getUsername())
				.password(PasswordCryptionFactory.getInstance().decrypt(readLogInfo.getCredentialInfo().getPassword()))
				.build(); 
		
		String charset = readLogInfo.getCharset();
		
		this.conn = conn;
		this.cmd = readLogInfo.getCommand();
		
		this.timeout = 10;
		this.client = SshClient.setUpDefaultClient();
		this.output = new LogComponentOutputStream(true);
        
		if (!StringUtils.isBlank(charset)) {
			logCharset = Charset.forName(charset);
		}

        logger.info("conn : {}, cmd : {}, logCharset : {}", conn, cmd, logCharset);
	}



	@Override
	public void run() {
		try {
			// Open the client
			client.start();
			

			ConnectFuture cf  = client.connect(conn.getUsername(), conn.getHostname(), conn.getPort());
			cf.await();

			try (ClientSession session = cf.verify().getSession()) {
				session.addPasswordIdentity(conn.getPassword());
				session.auth().verify(TimeUnit.SECONDS.toMillis(timeout));

				// Create the exec and channel its output/error streams
				try (ChannelExec channel = session.createExecChannel(cmd)) {
					channel.setOut(new NoCloseOutputStream(this.output));
					channel.setErr(new NoCloseOutputStream(this.output));
					WaitableFuture wf = channel.open();
					wf.await();

					Set<ClientChannelEvent> waitMask = channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), 0);

					if (waitMask.contains(ClientChannelEvent.TIMEOUT)) {
						throw new SocketTimeoutException("Failed to retrieve command result in time: " + cmd);
					}

					Integer exitStatus = channel.getExitStatus();
					
					if (exitStatus == null) {
						throw new SshException("Error executing command [" + cmd + "] over SSH [" + this.conn.getUsername() + "@" + this.conn.getHostname());
					}
				};
				session.close();
			}
		}catch(Exception e) {
			this.output.processLine(e.getMessage());
			logger.error("ssh error : {} ", e.getMessage(), e);
		} finally {
			client.stop();
			this.stopped = true; 
		}
	}

	public void stop() {
		try {
			this.client.stop();
			this.stopped = true; 
		} finally {
			this.client = null;
		}
		
	}

    public LogComponentOutputStream getOutputStream() {
    	return output; 
    }
    
    public boolean isStop() {
    	return this.stopped;
    }
}