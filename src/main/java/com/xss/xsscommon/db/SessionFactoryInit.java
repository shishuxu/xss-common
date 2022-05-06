package com.xss.xsscommon.db;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SessionFactoryInit {

	@Bean
	public DbConfig dbConfig() {
		return new DbConfig();
	}

	@Bean
	public SessionFactory sessionFactory() {
		return new SessionFactory(dbConfig());
	}
}
