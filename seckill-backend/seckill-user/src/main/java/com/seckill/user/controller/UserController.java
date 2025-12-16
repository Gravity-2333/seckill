package com.seckill.user.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.seckill.common.constant.RoleEnum;
import com.seckill.common.entity.PageInfo;
import com.seckill.common.exception.BusinessException;
import com.seckill.common.vo.ResultVO;
import com.seckill.user.entity.User;
import com.seckill.user.service.UserService;
import com.seckill.user.vo.TokenVO;
import com.seckill.user.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户模块Controller
 */
@RestController
@RequestMapping("/user")
@Api(tags = "用户管理接口")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    @ApiOperation("用户注册（匿名访问）")
    @ApiOperationSupport(author = "甲（组长）")
    public ResultVO<UserVO> register(@RequestBody User user) {
        return ResultVO.success(userService.register(user));
    }

    @PostMapping("/login")
    @ApiOperation("用户登录（匿名访问）")
    @ApiOperationSupport(author = "甲（组长）")
    public ResultVO<TokenVO> login(
            @ApiParam("用户名") @RequestParam String username,
            @ApiParam("密码") @RequestParam String password
    ) {
        return ResultVO.success(userService.login(username, password));
    }

    @GetMapping("/current")
    @ApiOperation("获取当前登录用户信息（登录用户）")
    @ApiOperationSupport(author = "甲（组长）")
// 新增：声明Authorization请求头参数
    @ApiImplicitParam(
            name = "Authorization",
            value = "JWT Token（格式：Bearer + 空格 + token）",
            required = true,
            paramType = "header", // 标记为请求头参数
            dataType = "String",
            example = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoyLCJ1c2VySWQiOjIsInVzZXJuYW1lIjoidGVzdCIsImlhdCI6MTc2NTg2NzI4OSwiZXhwIjoxNzY1ODc0NDg5fQ.Nwvyiauqy6TTMKz6yw8eH1gr5vRr"
    )
    public ResultVO<UserVO> getCurrentUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResultVO.success(userService.getCurrentUser(userId));
    }

    @GetMapping("/list")
    @ApiOperation("分页查询用户列表（仅管理员）")
    @ApiOperationSupport(author = "甲（组长）")
    // 新增：声明Authorization请求头参数
    @ApiImplicitParam(
            name = "Authorization",
            value = "JWT Token（格式：Bearer + 空格 + token）",
            required = true,
            paramType = "header", // 标记为请求头参数
            dataType = "String",
            example = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoyLCJ1c2VySWQiOjIsInVzZXJuYW1lIjoidGVzdCIsImlhdCI6MTc2NTg2NzI4OSwiZXhwIjoxNzY1ODc0NDg5fQ.Nwvyiauqy6TTMKz6yw8eH1gr5vRr"
    )
    public ResultVO<PageInfo<UserVO>> getUserPage(
            @ApiParam("页码") @RequestParam Integer pageNum,
            @ApiParam("每页条数") @RequestParam Integer pageSize,
            @ApiParam("用户名（模糊查询）") @RequestParam(required = false) String username,
            HttpServletRequest request
    ) {
        // 验证管理员权限
        Integer role = (Integer) request.getAttribute("role");
        if (!RoleEnum.ADMIN.getCode().equals(role)) {
            throw BusinessException.forbidden();
        }
        return ResultVO.success(userService.getUserPage(pageNum, pageSize, username));
    }

    @PutMapping("/status/{id}")
    @ApiOperation("禁用/启用用户（仅管理员）")
    @ApiOperationSupport(author = "甲（组长）")
    public ResultVO<Void> updateUserStatus(
            @ApiParam("用户ID") @PathVariable Long id,
            @ApiParam("状态") @RequestParam Integer status,
            HttpServletRequest request
    ) {
        // 验证管理员权限
        Integer role = (Integer) request.getAttribute("role");
        if (!RoleEnum.ADMIN.getCode().equals(role)) {
            throw BusinessException.forbidden();
        }
        userService.updateUserStatus(id, status);
        return ResultVO.success();
    }
}