package com.smartsite.netsdk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @author 信息化部-王浩
 * @description 功能描述
 * @create 2023/4/28 16:19
 */
@Configuration
@EnableAsync
public class ThreadConfig {
    @Bean("taskExecutor")
    public Executor myThreadExetor()
    {
        ThreadPoolTaskExecutor threadPoolTaskExecutor=new ThreadPoolTaskExecutor();
        ////核心线程数量
        threadPoolTaskExecutor.setCorePoolSize(5);
        ////最大线程数量
        threadPoolTaskExecutor.setMaxPoolSize(20);
        //线程闲置的时候存活的时间
        threadPoolTaskExecutor.setKeepAliveSeconds(60);
        threadPoolTaskExecutor.setThreadNamePrefix("baoliang_test");
        threadPoolTaskExecutor.initialize();
        return  threadPoolTaskExecutor;
    }
}
