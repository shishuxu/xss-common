package com.xss.xsscommon.db;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

@Slf4j
public class SessionFactory {

	private static class GroupInfo {
		AtomicLong seq;
		List<SqlSessionFactory> factories;

		GroupInfo(List<SqlSessionFactory> factories) {
			this.seq = new AtomicLong(0);
			this.factories = factories;
		}
	}

	private Object sessionGroupsLock = new Object();
	private Map<String, GroupInfo> sessionGroups;
	private Map<String, DbGroup> dbGroupConfs;
	private Set<String> mappers;

	private SqlSessionFactory createSessionFactory(DbGroup group, DbProp config) {
		String url;
		if (StringUtils.isNotBlank(config.getUrl())) {
			url = StringUtils.trimToEmpty(config.getUrl());
		} else {
			url = "jdbc:mysql://" + config.getHost() + ":" + config.getPort() + "/" + config.getName() + "?useUnicode=true&characterEncoding=utf8&useAffectedRows=true&serverTimezone=GMT%2B8&zeroDateTimeBehavior=convertToNull";
		}
		DruidDataSource dataSource = new DruidDataSource();

		if (StringUtils.isNotBlank(group.getDriver())) {
			dataSource.setDriverClassName(group.getDriver());
		}
		dataSource.setUrl(url);
		dataSource.setUsername(config.getUser());
		dataSource.setPassword(config.getPass());
		if (url.startsWith("jdbc:mysql:")) {
			List<String> initSqls = new ArrayList<>();
			initSqls.add("SET NAMES utf8mb4;");
			initSqls.add("SET sql_mode=(SELECT REPLACE(@@sql_mode,'ONLY_FULL_GROUP_BY',''));");
			dataSource.setConnectionInitSqls(initSqls);
		}
		if (null != group.getQueryTimeout()) {
			dataSource.setQueryTimeout(group.getQueryTimeout());
		}
		if (null != config.getPoolMaximumActiveConnections()) {
			dataSource.setMaxActive(config.getPoolMaximumActiveConnections());
		}
		if (null != config.getMinIdle()) {
			dataSource.setMinIdle(config.getMinIdle());
		}
		dataSource.setMaxWait(group.getMaxWait());
		dataSource.setTimeBetweenEvictionRunsMillis(2 * 1000);
		dataSource.setMinEvictableIdleTimeMillis(8 * 60 * 1000);
		dataSource.setMaxEvictableIdleTimeMillis(15 * 60 * 1000);
		dataSource.setValidationQuery("select 1");
		dataSource.setValidationQueryTimeout(3);
		dataSource.setTestWhileIdle(true);

		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment(config.getEnvironment(), transactionFactory, dataSource);
		Configuration configuration = new Configuration(environment);
		configuration.setVfsImpl(SpringBootVFS.class);

		// Supporting data module
		for (String p : this.mappers) {
			configuration.addMappers(p);
		}
		return new SqlSessionFactoryBuilder().build(configuration);
	}

	private List<SqlSessionFactory> initSqlSessionFactory(DbGroup g) {
		if (null == g.getList() || g.getList().isEmpty()) return Collections.emptyList();
		List<SqlSessionFactory> l = new ArrayList<>();
		for (DbProp p : g.getList()) {
			l.add(createSessionFactory(g, p));
		}
		return l;
	}

	public SessionFactory(DbConfig config) {
		this.dbGroupConfs = new HashMap<>();
		this.sessionGroups = new HashMap<>();
		this.mappers = new HashSet<>();
		this.initConfig(config);
	}

	private void initConfig(DbConfig config) {
		if (null != config) {
			if (null != config.getDbMappers()) this.mappers.addAll(config.getDbMappers());
			if (null != config.getDbGroups()) {
				for (DbGroup g : config.getDbGroups()) {
					this.dbGroupConfs.put(g.getName(), g);
				}
			}
		}
	}

	public void addMapper(String mapper) {
		if (this.mappers.add(mapper)) {
			synchronized (sessionGroupsLock) {
				for (GroupInfo gi : sessionGroups.values()) {
					for (SqlSessionFactory f : gi.factories) {
						f.getConfiguration().addMappers(mapper);
					}
				}
			}
		}
	}

	public void addMapper(List<String> mappers) {
		if (null == mappers || mappers.isEmpty()) return;
		for (String m : mappers) {
			addMapper(m);
		}
	}

	private GroupInfo getSessionGroupFromConfig(String name) {
		if (null == name) name = DbGroup.defaultGroup;
		DbGroup g = dbGroupConfs.get(name);
		if (null == g) return null;
		List<SqlSessionFactory> l = initSqlSessionFactory(g);
		if (null == l || l.isEmpty()) return null;
		return new GroupInfo(l);
	}

	private SqlSession getOneSlaveSession(GroupInfo g) {
		if (null == g || g.factories.isEmpty())
			return null;
		return g.factories.get(new Long(Math.abs(g.seq.getAndIncrement()) % g.factories.size()).intValue()).openSession();
	}

	public SessionContext getSessionContext(String name) {
		if (null == name) name = DbGroup.defaultGroup;
		GroupInfo g = sessionGroups.get(name);
		if (null == g) {
			synchronized (sessionGroupsLock) { // 把synchronize放在这里是减少锁等待
				g = sessionGroups.get(name);
				if (null == g) { // 为了避免多线程冲突,所以这里需要再判断一次 null == g
					Map<String, GroupInfo> newGroups = new HashMap<>(sessionGroups);
					g = getSessionGroupFromConfig(name);
					if (null == g) return null;
					newGroups.put(name, g);
					sessionGroups = newGroups;
				}
			}
		}
		return new SessionContext(getOneSlaveSession(g));
	}

	public SessionContext getSessionContext() {
		return getSessionContext(null);
	}

	public <T> CompletableFuture<T> execute(Function<SessionContext, T> func, String name) {
		return executeMultiSession(sessions -> func.apply(sessions.get(name)), name);
	}

	public <T> CompletableFuture<T> executeMultiSession(Function<Map<String, SessionContext>, T> func, String... names) {
		CompletableFuture<T> d = new CompletableFuture<>();
		Map<String, SessionContext> sessions = new HashMap<>();
		try {
			for (String n : names) {
				sessions.put(n, getSessionContext(n));
			}
			T r = func.apply(sessions);
			d.complete(r);
		} catch (Throwable e) {
			d.completeExceptionally(e);
		} finally {
			for (Map.Entry<String, SessionContext> s : sessions.entrySet()) {
				SessionContext.closeSilently(s.getValue());
			}
		}
		return d;
	}
}
