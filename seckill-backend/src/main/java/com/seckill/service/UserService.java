package com.seckill.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.seckill.common.PageQuery;
import com.seckill.dto.UserLoginDTO;
import com.seckill.dto.UserRegisterDTO;
import com.seckill.entity.User;
import com.seckill.vo.LoginVO;
import com.seckill.vo.UserVO;

public interface UserService {
    
    void register(UserRegisterDTO registerDTO);
    
    LoginVO login(UserLoginDTO loginDTO);
    
    UserVO getCurrentUser(String token);
    
    IPage<UserVO> getUserList(PageQuery pageQuery, String username);
    
    void updateUserStatus(Long id, Integer status);
    
    User getUserByUsername(String username);
}