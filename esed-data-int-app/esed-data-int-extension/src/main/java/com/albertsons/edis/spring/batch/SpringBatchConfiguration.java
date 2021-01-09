package com.albertsons.edis.spring.batch;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class SpringBatchConfiguration {
    private static Logger logger = LoggerFactory.getLogger(SpringBatchConfiguration.class.getName());

    @Bean
    public PlatformTransactionManager transactionManager() {
        // DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        // transactionManager.setDataSource(dataSourceConfiguration.getDataSource("DEV"));
        // return transactionManager;
        return new ResourcelessTransactionManager();
    }

    @Bean
    public MapJobRepositoryFactoryBean repositoryFactoryBean(PlatformTransactionManager transactionManager) {
        MapJobRepositoryFactoryBean mapJobRepositoryFactoryBean = new MapJobRepositoryFactoryBean(transactionManager);
        mapJobRepositoryFactoryBean.setTransactionManager(transactionManager);
        return mapJobRepositoryFactoryBean;
    }

    @Bean
    public JobRepository jobRepository(MapJobRepositoryFactoryBean factoryBean) throws Exception {
        return factoryBean.getObject();
    }

    @Bean
    public JobBuilderFactory jobBuilderFactory(JobRepository jobRepository) {
        return new JobBuilderFactory(jobRepository);
    }

    @Bean
    public StepBuilderFactory stepBuilderFactory(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilderFactory(jobRepository, transactionManager);
    }

    @Bean
    public JobRegistry jobRegistry(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) throws Exception {
        return new MapJobRegistry();
    }

    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
        SimpleJobLauncher simpleJobLauncher = new SimpleJobLauncher();
        simpleJobLauncher.setJobRepository(jobRepository);
        simpleJobLauncher.afterPropertiesSet();
        return simpleJobLauncher;
    }

    @Bean
    public JobExplorer jobExplorer(MapJobRepositoryFactoryBean repositoryFactory) {
        return new SimpleJobExplorer(repositoryFactory.getJobInstanceDao(), repositoryFactory.getJobExecutionDao(),
                repositoryFactory.getStepExecutionDao(), repositoryFactory.getExecutionContextDao());
    }

    @Bean
    public JobOperator jobOperator(JobExplorer jobExplorer, JobRepository jobRepository,
                                   JobRegistry jobRegistry, JobLauncher jobLauncher) {

        SimpleJobOperator jobOperator = new SimpleJobOperator();
        jobOperator.setJobExplorer(jobExplorer);
        jobOperator.setJobRepository(jobRepository);
        jobOperator.setJobRegistry(jobRegistry);
        jobOperator.setJobLauncher(jobLauncher);
        return jobOperator;
    }

    @Bean
    SpringBatchService springBatchService(JobRegistry jobRegistry,
                                          JobBuilderFactory jobBuilderFactory,
                                          StepBuilderFactory stepBuilderFactory,
                                          JobLauncher jobLauncher) {

        return new SpringBatchService(jobRegistry, jobBuilderFactory, stepBuilderFactory, jobLauncher);
    }

}

