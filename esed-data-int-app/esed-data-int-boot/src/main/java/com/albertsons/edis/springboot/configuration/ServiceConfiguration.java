package com.albertsons.edis.springboot.configuration;

import com.albertsons.edis.*;
import com.albertsons.edis.springboot.service.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jndi.JndiTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ServiceConfiguration {
    private Environment environment;

    private File serverHome;

    @Autowired
    public ServiceConfiguration(Environment environment) {
        this.environment = environment;

        URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
        if ("jar".equalsIgnoreCase(url.getProtocol())) {
            String path = url.getPath();
            int index = path.indexOf(".jar");
            path = path.substring(0, index) + ".jar";
            try {
                path = new URI(path).getPath();
                serverHome = new File(path);
                if (serverHome.exists()) {
                    serverHome = serverHome.getParentFile().getParentFile();
                }

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        } else {
            File root = new File(System.getProperty("user.home"));
            if (environment.getProperty("edis.server.home") == null) {
                serverHome = new File(root, "Application/esed-data-int-app");
            } else {
                serverHome = new File(environment.getProperty("edis.server.home"));
            }
        }

        System.setProperty("edis.server.home", serverHome.getAbsolutePath());

    }

    @Bean("encryptor")
    public StringEncryptor stringEncryptor() {
        Properties defaultProperties = new Properties();
        try {
            defaultProperties.load(getClass().getClassLoader().getResourceAsStream("jasypt.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String configFile = environment.getProperty("jasypt.encryptor.config-file") != null ? environment.getProperty("jasypt.encryptor.config-file") : "conf/jasypt.properties";
        File file = new File(serverHome, configFile);
        Properties properties = new Properties(defaultProperties);
        if (file.exists()) {
            try {
                properties.load(new FileInputStream(file));
            } catch (IOException e) {

            }

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
    PipelineScheduler pipelineScheduler(Scheduler scheduler) {
        final String groupName = "PIPELINE_PROCESSOR";

        return new PipelineScheduler() {
            @Override
            public void schedule(PipelineProcessor processor, String name, String calendar, long delay) {
                JobDataMap dataMap = new JobDataMap();
                dataMap.putIfAbsent("PROCESSOR", processor);

                JobDetail job = JobBuilder.newJob().newJob(PipelineExecutionJob.class)
                        .withIdentity(name, groupName)
                        .setJobData(dataMap)
                        .build();

                ScheduleBuilder scheduleBuilder = null;
                try {
                    scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(Integer.parseInt(calendar))
                            .repeatForever();

                } catch (Exception ex) {
                    scheduleBuilder = CronScheduleBuilder.cronSchedule(calendar);
                }

                Trigger scanTrigger = TriggerBuilder.newTrigger().withIdentity(name, groupName)
                        .startAt(new Date(System.currentTimeMillis() + delay))
                        .withSchedule(scheduleBuilder)
                        .build();

                try {
                    scheduler.scheduleJob(job, scanTrigger);

                } catch (SchedulerException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void unschedule(String pipeline) {

            }
        };
    }

    static class PipelineExecutionJob implements Job {

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            PipelineProcessor processor = (PipelineProcessor) context.getJobDetail().getJobDataMap().get("PROCESSOR");
            try {
                processor.process();
            } catch (PipelineProcessException e) {
                throw new JobExecutionException(e);
            }
        }
    }

    @Bean
    PipelineContext pipelineContext(ApplicationContext applicationContext) {
        return applicationContext::getBean;
    }

    @Bean
    PipelineDeployer pipelineDeployer(PipelineContext pipelineContext) {
        File pipelineHome = new File(serverHome, "pipeline");
        if (!pipelineHome.exists()) {
            pipelineHome.mkdirs();
        }

        return new URLPipelineDeployer(pipelineHome, 100, pipelineContext);
    }

    @Bean
    ExceptionHandlingService exceptionHandlingService() {
        return new ExceptionHandlingService() {
            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }
        };
    }

    @Bean
    PipelineProcessService pipelineProcessorManager(PipelineDeployer deployer, ExecutorService executorService, ExceptionHandlingService exceptionHandlingService) {
        return new PipelineProcessorManager(deployer, executorService, exceptionHandlingService);
    }

}
