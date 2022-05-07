package com.xss.xsscommon.db;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 *
 * @author xss
 * @version 1.0.0
 * @date 2022-04-28 17:00
 */
/*註解保留時機*/
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
/*作用類型*/
@Target({java.lang.annotation.ElementType.TYPE})
@Documented
@Import(SessionFactoryInit.class)
@Configuration
public @interface EnableDb {


}
