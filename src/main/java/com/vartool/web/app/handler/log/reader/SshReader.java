package com.vartool.web.app.handler.log.reader;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.time.Duration;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.SshException;
import org.apache.sshd.common.config.keys.FilePasswordProvider;
import org.apache.sshd.common.config.keys.loader.KeyPairResourceLoader;
import org.apache.sshd.common.future.WaitableFuture;
import org.apache.sshd.common.session.SessionHeartbeatController.HeartbeatType;
import org.apache.sshd.common.util.io.output.NoCloseOutputStream;
import org.apache.sshd.common.util.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vartech.common.utils.StringUtils;
import com.vartool.core.crypto.PasswordCryptionFactory;
import com.vartool.web.app.handler.log.LogCmpManager;
import com.vartool.web.app.handler.log.stream.LogComponentOutputStream;
import com.vartool.web.constants.CredentialsType;
import com.vartool.web.dto.CredentialInfo;
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
	private ReadLogInfo readLogInfo;
	
	private ConnectFuture connectFuture;
	private CredentialInfo credentialInfo;
	
	public SshReader(ReadLogInfo readLogInfo) {
		
		CredentialInfo credentialInfo = readLogInfo.getCredentialInfo();
		
		SshConnectionInfo conn = SshConnectionInfo.builder()
				.hostname(readLogInfo.getRemoteHost())
				.port(readLogInfo.getRemotePort())
				.build();
		
		if(credentialInfo != null) {
			this.credentialInfo = credentialInfo; 
			conn.setUsername(credentialInfo.getUsername());
			conn.setPassword(PasswordCryptionFactory.getInstance().decrypt(readLogInfo.getCredentialInfo().getPassword()));
		}
		
		String charset = readLogInfo.getCharset();
		this.readLogInfo = readLogInfo; 
		this.conn = conn;
		this.cmd = readLogInfo.getCommand();
		
		this.timeout = 10;
		this.client = SshClient.setUpDefaultClient();
		
		if (!StringUtils.isBlank(charset)) {
			logCharset = Charset.forName(charset);
		}
		
		this.output = new LogComponentOutputStream(logCharset);

        logger.info("conn : {}, cmd : {}, logCharset : {}", conn, cmd, logCharset);
	}

	@Override
	public void run() {
		try {
			// Open the client
			client.start();
			
			client.setSessionHeartbeat(HeartbeatType.IGNORE, Duration.ofSeconds(60));

			connectFuture  = client.connect(conn.getUsername(), conn.getHostname(), conn.getPort());
			connectFuture.await();
			
			try (ClientSession session = connectFuture.verify().getSession()) {
				
				if(this.credentialInfo != null && CredentialsType.SECRET_TEXT.equals(CredentialsType.getLogType(this.credentialInfo.getCredType()))) {
					KeyPairResourceLoader loader = SecurityUtils.getKeyPairResourceParser();
					Collection<KeyPair> kps = loader.loadKeyPairs(null, null, FilePasswordProvider.of(""), this.credentialInfo.getSecretText());
					session.addPublicKeyIdentity(kps.stream().findFirst().get());
				}else {
					session.addPasswordIdentity(conn.getPassword());
				}
				
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
				}catch(Exception e) {
					throw new Exception(e);
				}
				session.close();
			}catch(Exception e) {
				throw new Exception(e);
			}
		}catch(Throwable e) {
			String msg = e.getMessage();
			LogCmpManager.getInstance().exception(this.readLogInfo.getCmpId(),"error : log server [" +this.conn.getUsername()+"@"+ this.conn.getHostname()+":"+ this.conn.getPort()+"] message : "+ msg);
			logger.error("ssh error : {} ", e.getMessage(), e);
		} finally {
			LogCmpManager.getInstance().stopTail(this.readLogInfo.getCmpId());
			if(client != null) {
				client.stop();
			}
			
			this.stopped = true;
		}
	}

	public void stop() {
		try {
			try {
				connectFuture.cancel();
			}catch(Exception e) {
				System.out.println("connectFuture error message : " + e.getMessage());
			}
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