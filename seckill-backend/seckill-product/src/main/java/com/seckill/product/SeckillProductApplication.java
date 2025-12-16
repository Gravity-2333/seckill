package com.seckill.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.seckill.product.mapper")
@ComponentScan("com.seckill")
public class SeckillProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeckillProductApplication.class, args);
    }
}