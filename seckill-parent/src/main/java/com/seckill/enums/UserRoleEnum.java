package com.seckill.enums;

public enum UserRoleEnum {
    ADMIN(1, "管理员"),
    USER(2, "普通用户");

    private final Integer code;
    private final String desc;

    UserRoleEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static UserRoleEnum getByCode(Integer code) {
        for (UserRoleEnum role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return null;
    }
}