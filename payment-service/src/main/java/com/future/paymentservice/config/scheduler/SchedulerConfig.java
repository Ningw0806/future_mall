package com.future.paymentservice.config.scheduler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class SchedulerConfig {

    @Bean("paymentTaskExecutor")
    public Executor paymentTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);    // core pool size
        executor.setMaxPoolSize(10);     // max number of threads
        executor.setQueueCapacity(100);  // queue size
        executor.setThreadNamePrefix("PaymentTask-");
        executor.initialize();
        return executor;
    }
}
