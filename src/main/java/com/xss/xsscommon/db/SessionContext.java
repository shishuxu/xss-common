package com.xss.xsscommon.db;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

import java.sql.ResultSet;

@Slf4j
public class SessionContext {
	private SqlSession session = null;

	public SessionContext(SqlSession s) {
		this.session = s;
	}

	public static void closeSilently(SessionContext ctx) {
		if (ctx == null) {
			return;
		}

		rollbackSilently(ctx);

		try {
			ctx.close();
		} catch (Throwable e) {
			LoggerUtil.error(log, "closeSilently", e);
		}
	}

	public static void rollbackSilently(SessionContext ctx) {
		if (ctx == null) {
			return;
		}

		try {
			ctx.rollback();
		} catch (Throwable e) {
			LoggerUtil.error(log, "rollbackSilently", e);
		}
	}

	public void commit() {
		if (session != null) {
			session.commit();
		}
	}

	public void rollback() {
		if (session != null) {
			session.rollback();
		}
	}

	public void close() {
		if (session != null) {
			session.close();
			session = null;
		}
	}

	public ResultSet query(String sql) throws Exception {
		return session.getConnection().createStatement().executeQuery(sql);
	}

	public <T> T getMapper(Class<T> clazz) {
		return session.getMapper(clazz);
	}
}
