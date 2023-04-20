package com.backend.stock.priceengine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EnableAsync
@Configuration
class TaskPoolConfig {

    @Bean
    public ExecutorService taskExecutor(){
        ExecutorService executorService= Executors.newFixedThreadPool(5);
        return executorService;
    }
}