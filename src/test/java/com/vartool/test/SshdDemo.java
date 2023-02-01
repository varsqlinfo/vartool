package com.vartool.test;

import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.security.PublicKey;
import java.util.EnumSet;
import java.util.Set;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.client.keyverifier.DefaultKnownHostsServerKeyVerifier;
import org.apache.sshd.client.keyverifier.KnownHostsServerKeyVerifier;
import org.apache.sshd.client.keyverifier.ServerKeyVerifier;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.future.WaitableFuture;
import org.apache.sshd.common.util.io.output.NoCloseOutputStream;

public class SshdDemo {

    public static void main(String[] args) throws Exception {
//        String username = "demo";
//        String password = "password";
//        String host = "test.rebex.net";
        String username = "lsncsm01";
        String password = "ehdwpfus2020$Lpldev";
        String host = "165.186.170.189";
        int port = 22;
        long defaultTimeoutSeconds = 10l;
        String command = "Get-Content \"C:\\zzz\\logs\\vai-app.log\" -Wait -Tail 10";
        
        int code = listFolderStructure(username, password, host, port, defaultTimeoutSeconds, command, false);
        
        System.out.println("------------------------");
        System.out.println("code : "+ code);
        
        System.out.println("");
    }

    public static int listFolderStructure(String username, String password, String host, int port, long defaultTimeoutSeconds, String command, boolean strictHostKey) throws Exception {
    	SshClient client = SshClient.setUpDefaultClient();
    	
    	new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					KnownHostsServerKeyVerifier verifier = new DefaultKnownHostsServerKeyVerifier(new ServerKeyVerifier() {
		                 @Override
		                 public boolean verifyServerKey(ClientSession clientSession, SocketAddress remoteAddress, PublicKey serverKey) {
		                     return !strictHostKey;
		                 }
		             }, true);

		             //client.setServerKeyVerifier(verifier);
		             client.start();
		             
		             ConnectFuture cf = client.connect(username, host, port);
		             cf.await();
		             
		             try (ClientSession session = cf.getSession()) {
		            	 session.addPasswordIdentity(password);
		            	 session.auth().verify(10000L);
		            	 
		            	 try (ClientChannel channel = session.createExecChannel(command)) {
		            		 NoCloseOutputStream out = new NoCloseOutputStream(System.out);
		                     channel.setOut(out);
		                     channel.setErr(out);
		                     channel.open();
//		                     WaitableFuture wf = channel.open();
//		                     wf.await();
		                     
		                     System.out.println("11111111");

		                     Set<ClientChannelEvent> waitMask = channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED, ClientChannelEvent.TIMEOUT), 0L);

		                     if (waitMask.contains(ClientChannelEvent.TIMEOUT)) {
		                         throw new SocketTimeoutException("Failed to retrieve command result in time: " + command);
		                     }
		                     
		                     Integer exitStatus = channel.getExitStatus();
		                     channel.close(false);
		            	 }finally {
		            		 System.out.println("111111");
		            		 session.close(false);
		            	 }
		             }finally {
		            	 System.out.println("2222123123123----");
		                 client.stop();
		                 if(client !=null) client.close();
		                 
		                 System.out.println("222212312asdfasdf3123----");
		             }
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
    	}).start();
    	
    	Thread.sleep(3000);
    	System.out.println("111-1");
    	client.stop();
    	System.out.println("111-2");
    	client.close(true);
    	System.out.println("111-3");
    	
    	return -1;
    	
    }

}