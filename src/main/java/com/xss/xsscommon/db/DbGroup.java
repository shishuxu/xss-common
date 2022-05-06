package com.xss.xsscommon.db;

import lombok.Data;

import java.util.List;

/**
 *
 * @author xss
 * @version 1.0.0
 * @date 2022-04-28 17:00
 */
@Data
public class DbGroup {
	static final String defaultGroup = "default";
	private String name;
	private String driver;
	private Integer queryTimeout = 10 * 60;
	private Long maxWait = 30 * 1000L;
	private List<DbProp> list;

}
