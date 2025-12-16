package com.seckill.user.vo;

import lombok.Data;

/**
 * 登录返回Token VO
 */
@Data
public class TokenVO {
    /**
     * JWT Token
     */
    private String token;
    /**
     * 用户基本信息
     */
    private UserVO user;
}