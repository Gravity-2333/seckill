package com.seckill.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 用户模块启动类
 */
@SpringBootApplication
@MapperScan("com.seckill.user.mapper")
@ComponentScan("com.seckill") // 扫描common模块组件
public class SeckillUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeckillUserApplication.class, args);
    }
}