package com.htsc.mdc.model.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created By 19921227 on 2025/2/20 14:02
 * 数据源配置
 */
@Configuration
@Data
@Slf4j
@ConfigurationProperties(prefix = "spring.datasource.druid")
public class DataSourceConfig {

    private String url;
    private String username;
    private String password;
    private String driverClassName;
    private int initialSize;
    private int minIdle;
    private int maxActive;
    private int maxWait;
    private int timeBetweenEvictionRunsMillis;
    private int minEvictableIdleTimeMillis;
    private int maxEvictableIdleTimeMillis;
    private String validationQuery;
    private boolean testWhileIdle;
    private boolean testOnBorrow;
    private boolean testOnReturn;
    private int removeAbandonedTimeout;
    private boolean removeAbandoned;
    private boolean keepAlive;
    private String connectProperties;
    private boolean breakAfterAcquireFailure;
    private int connectionErrorRetryAttempts;
    private int notFullTimeoutRetryCount;
    private int acquireRetryDelay;
    private boolean failFast;

    @Bean
    public DataSource dataSource() {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(url);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setDriverClassName(driverClassName);
        datasource.setInitialSize(initialSize);
        datasource.setMinIdle(minIdle);
        datasource.setMaxActive(maxActive);
        datasource.setMaxWait(maxWait);
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        datasource.setMaxEvictableIdleTimeMillis(maxEvictableIdleTimeMillis);
        datasource.setValidationQuery(validationQuery);
        datasource.setTestWhileIdle(testWhileIdle);
        datasource.setTestOnBorrow(testOnBorrow);
        datasource.setTestOnReturn(testOnReturn);
        datasource.setRemoveAbandonedTimeout(removeAbandonedTimeout);
        datasource.setRemoveAbandoned(removeAbandoned);
        datasource.setKeepAlive(keepAlive);
        Properties properties = new Properties();
        properties.put("druid.stat.mergeSql=true;druid.stat.slowSqlMillis", 5000);
        datasource.setConnectProperties(properties);
        datasource.setBreakAfterAcquireFailure(breakAfterAcquireFailure);
        datasource.setConnectionErrorRetryAttempts(connectionErrorRetryAttempts);
        datasource.setNotFulLTimeoutRetryCount(notFullTimeoutRetryCount);
        log.info("datasource===MaxActive:{}, ValidationQuery:{},NotFullTimeoutRetryCount:{}",
                datasource.getMaxActive(), datasource.getValidationQuery(), datasource.getNotFullTimeoutRetryCount());
        return datasource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }
}
