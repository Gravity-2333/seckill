package com.seckill.user.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户返回VO（隐藏密码）
 */
@Data
public class UserVO {
    private Long id;
    private String username;
    private String phone;
    private String email;
    private Integer role;
    private LocalDateTime createTime;
}