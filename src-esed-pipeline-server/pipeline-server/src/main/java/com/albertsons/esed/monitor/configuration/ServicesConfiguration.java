package com.albertsons.esed.monitor.configuration;

import com.albertsons.esed.monitor.server.PipelineContext;
import com.albertsons.esed.monitor.server.PipelineDeploymentService;
import com.albertsons.esed.monitor.server.PipelineServer;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.http.ssl.SSLContextBuilder;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ServicesConfiguration {

    @Autowired
    private Environment environment;

    @Bean("encryptor")
    public StringEncryptor stringEncryptor() {

        Properties defaultProperties = new Properties();
        try {
            defaultProperties.load(getClass().getClassLoader().getResourceAsStream("jasypt.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        String configFile = environment.getProperty("jasypt.encryptor.config-file") != null ? environment.getProperty("jasypt.encryptor.config-file") : "conf/jasypt.properties";
        File file = new File(PipelineServer.getInstance().getHome(), configFile);
        Properties properties = new Properties(defaultProperties);
        try {
            properties.load(new FileInputStream(file));

        } catch (IOException e) {

        }

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(properties.getProperty("jasypt.encryptor.password"));
        config.setAlgorithm(properties.getProperty("jasypt.encryptor.algorithm"));
        config.setKeyObtentionIterations(properties.getProperty("jasypt.encryptor.key-obtention-iterations"));
        config.setPoolSize(properties.getProperty("jasypt.encryptor.pool-size"));
        config.setProviderName(properties.getProperty("jasypt.encryptor.provider-name"));
        config.setSaltGeneratorClassName(properties.getProperty("jasypt.encryptor.salt-generator-classname"));
        config.setStringOutputType(properties.getProperty("jasypt.encryptor.string-output-type"));
        encryptor.setConfig(config);
        return encryptor;
    }

    @Bean
    ExecutorService executorService() {
        return Executors.newFixedThreadPool(50);
    }

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

    //@Bean
    SSLSocketFactory sslSocketFactory() throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException, KeyManagementException {
        File keyStore = new File(environment.getProperty("pipeline.server.conf.dir"), "keystore.jks");
        String password = "allpassword";

        //String password = environment.getProperty("server.ssl.key-store-password");

        SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(keyStore, password.toCharArray())
                .build();

        return sslContext.getSocketFactory();
    }

    @Bean
    PipelineContext pipelineContext(ApplicationContext applicationContext) {
        PipelineContext pipelineContext = new PipelineContext() {
            @Override
            public <T> T getService(Class<T> serviceType) {
                return applicationContext.getBean(serviceType);
            }
        };
        return pipelineContext;
    }

}
