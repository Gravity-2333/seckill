package com.seckill.common.config;


import com.seckill.common.utils.MinioUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class MinioConfig {

    @Bean
    @ConfigurationProperties(prefix = "minio")
    public MinioUtil minioUtil(){
        return new MinioUtil();
    }
}
