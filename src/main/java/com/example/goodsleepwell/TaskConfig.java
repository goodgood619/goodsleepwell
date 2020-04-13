package com.example.goodsleepwell;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Configuration
@EnableAsync
public class TaskConfig {
    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5);
        taskExecutor.setMaxPoolSize(50);
        taskExecutor.setQueueCapacity(1001);
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(60);
        taskExecutor.setThreadNamePrefix("Executor-");
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Bean
    public ThreadPoolTaskExecutor one() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(6);
        taskExecutor.setMaxPoolSize(50);
        taskExecutor.setQueueCapacity(1001);
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(60);
        taskExecutor.setThreadNamePrefix("oneExecutor-");
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Bean
    public ThreadPoolTaskExecutor two() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(6);
        taskExecutor.setMaxPoolSize(50);
        taskExecutor.setQueueCapacity(1001);
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(60);
        taskExecutor.setThreadNamePrefix("twoExecutor-");
        taskExecutor.initialize();
        return taskExecutor;
    }
    @Bean
    public ThreadPoolTaskExecutor three() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(6);
        taskExecutor.setMaxPoolSize(50);
        taskExecutor.setQueueCapacity(1001);
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(60);
        taskExecutor.setThreadNamePrefix("threeExecutor-");
        taskExecutor.initialize();
        return taskExecutor;
    }
}

