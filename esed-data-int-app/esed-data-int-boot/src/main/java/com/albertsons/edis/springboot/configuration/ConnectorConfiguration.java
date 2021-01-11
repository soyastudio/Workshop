package com.albertsons.edis.springboot.configuration;

import com.albertsons.edis.DataAccessService;
import com.albertsons.edis.JmsMessagePublishService;
import com.albertsons.edis.springboot.service.DataAccessServiceImpl;
import com.albertsons.edis.springboot.service.MessagePublishServiceImpl;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jndi.JndiTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

//@Configuration
public class ConnectorConfiguration {
    @Autowired
    Environment environment;

    @Bean
    PlatformTransactionManager txManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    DataSource dataSource() {
        HikariConfig dataSourceConfig = new HikariConfig();
        dataSourceConfig.setDriverClassName(environment.getRequiredProperty("spring.datasource.driver-class-name"));
        dataSourceConfig.setJdbcUrl(environment.getRequiredProperty("spring.datasource.url"));
        dataSourceConfig.setUsername(environment.getRequiredProperty("spring.datasource.username"));
        dataSourceConfig.setPassword(
                environment.getRequiredProperty("spring.datasource.password"));
        dataSourceConfig.setMaximumPoolSize(Integer.parseInt(environment.getRequiredProperty("spring.datasource.hikari.maximumPoolSize")));
        dataSourceConfig.setMinimumIdle(Integer.parseInt(environment.getRequiredProperty("spring.datasource.hikari.minIdle")));
        dataSourceConfig.setIdleTimeout(Long.parseLong(environment.getRequiredProperty("spring.datasource.hikari.idleTimeout")));

        dataSourceConfig.addDataSourceProperty("poolName", "ESED");
        dataSourceConfig.addDataSourceProperty("cachePrepStmts", "true");
        dataSourceConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        dataSourceConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return new HikariDataSource(dataSourceConfig);
    }

    @Bean
    DataAccessService dataAccessService(DataSource dataSource) {
        return new DataAccessServiceImpl(dataSource);
    }

    @Bean
    JndiTemplate jndiTemplate(Environment environment) {
        Properties properties = new Properties();
        properties.setProperty("java.naming.factory.initial", environment.getProperty("java.naming.factory.initial"));
        properties.setProperty("java.naming.provider.url", environment.getProperty("java.naming.provider.url"));
        return new JndiTemplate(properties);
    }

    @Bean
    JmsMessagePublishService messagePublishService(JndiTemplate jndiTemplate) {
        return new MessagePublishServiceImpl(jndiTemplate);
    }
}
