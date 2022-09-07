package com.vartool.web.app.handler.log.reader;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(exclude = {"password"})
public class SshConnectionInfo {

	private int port;
	private String username;
	private String password;
	private String hostname;

	@Builder
	public SshConnectionInfo(int port, String username, String password, String hostname) {
		this.port = port;
		this.username = username;
		this.password = password;
		this.hostname = hostname;
	}
}