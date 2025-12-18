package com.seckill.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.user.entity.User;
import com.seckill.user.vo.UserVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户Mapper
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 分页查询用户列表（管理员）- 注解SQL
     */
    @Select("SELECT id, username, phone, email, role, create_time AS createTime " +
            "FROM t_user " +
            "WHERE (#{username} IS NULL OR #{username} = '' OR username LIKE CONCAT('%', #{username}, '%'))")
    IPage<UserVO> selectUserPage(Page<UserVO> page, @Param("username") String username);

    /**
     * 根据用户名查询用户 - 注解SQL
     */
    @Select("SELECT * FROM t_user WHERE username = #{username}")
    User selectByUsername(@Param("username") String username);
}