package com.xss.xsscommon.db;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;
@Data
public class DbProp {
	private static final AtomicInteger IDX = new AtomicInteger(0);
	private String environment;
	private String url;
	private String host;
	private Integer port = 3306;
	private String name;
	private String user;
	private String pass;
	private Integer poolMaximumActiveConnections;
	@Deprecated
	private Integer poolMaximumIdleConnections;
	private Integer minIdle;


	public String getEnvironment() {
		return null == environment ? environment = "dev-" + IDX.incrementAndGet() : environment;
	}

	public Integer getPort() {
		return null == port ? 3306 : port;
	}

}
