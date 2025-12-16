package com.seckill.common.exception;

import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class BusinessException extends RuntimeException {
    private final Integer code;

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    // 常用异常快捷创建
    public static BusinessException unAuthorized() {
        return new BusinessException(401, "未登录或token已过期");
    }

    public static BusinessException forbidden() {
        return new BusinessException(403, "无权限访问");
    }

    public static BusinessException badRequest(String msg) {
        return new BusinessException(400, msg);
    }

    public static BusinessException serverError() {
        return new BusinessException(500, "服务器内部错误");
    }
}