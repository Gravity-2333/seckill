package com.seckill.common.interceptor;

import com.seckill.common.exception.BusinessException;
import com.seckill.common.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT登录拦截器
 */
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 1. 获取token（请求头：Authorization，格式：Bearer xxx）
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw BusinessException.unAuthorized();
        }
        String token = authHeader.substring(7);

        // 2. 验证token
        if (!jwtUtil.validateToken(token)) {
            throw BusinessException.unAuthorized();
        }

        // 3. 将用户信息存入request（方便后续获取）
        request.setAttribute("userId", jwtUtil.getUserIdFromToken(token));
        request.setAttribute("role", jwtUtil.getRoleFromToken(token));
        return true;
    }
}