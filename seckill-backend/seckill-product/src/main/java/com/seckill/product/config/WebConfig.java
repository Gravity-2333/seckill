package com.seckill.product.config;

import com.seckill.common.interceptor.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置（拦截器注册）
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册JWT拦截器：默认拦截商品模块接口，排除匿名可访问接口
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/product/**")
                // 匿名接口（所有用户可访问）
                .excludePathPatterns(
                        "/product/list",
                        "/product/category/list"
                );
    }
}
