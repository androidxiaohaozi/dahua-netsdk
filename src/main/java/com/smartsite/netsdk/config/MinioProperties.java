package com.smartsite.netsdk.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 信息化部-王浩
 * @description minio 配置类
 * @create 2023/5/5 11:52
 */
@Component
@ConfigurationProperties(prefix = "minio")
@Data
public class MinioProperties {
    private String url;
    private String accessKey;
    private String secretKey;
}
