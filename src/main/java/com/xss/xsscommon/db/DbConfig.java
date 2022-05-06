package com.xss.xsscommon.db;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 *
 * @author xss
 * @version 1.0.0
 * @date 2022-04-28 17:00
 */
@Data
@ConfigurationProperties
public class DbConfig {
    List<String> dbMappers;
    List<DbGroup> dbGroups;

}
