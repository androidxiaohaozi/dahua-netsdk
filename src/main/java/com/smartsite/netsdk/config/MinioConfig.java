package com.smartsite.netsdk.config;

import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author 信息化部-王浩
 * @description 功能描述
 * @create 2023/5/5 11:52
 */
@Configuration
public class MinioConfig {

    @Resource
    private MinioProperties clientConfig;

    @Bean
    public MinioClient getMinioClient() throws InvalidPortException, InvalidEndpointException {
        return new MinioClient(
                clientConfig.getUrl(),
                clientConfig.getAccessKey(),
                clientConfig.getSecretKey()
        );
    }
}