package com.seckill.seckillparent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// 显式指定扫描com.seckill根包，覆盖config、mapper、seckillparent等子包
@SpringBootApplication(scanBasePackages = "com.seckill")
@EnableScheduling
@MapperScan("com.seckill.mapper")
public class SeckillParentApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeckillParentApplication.class, args);
    }

}