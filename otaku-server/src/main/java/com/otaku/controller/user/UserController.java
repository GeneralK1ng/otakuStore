package com.otaku.controller.user;


import com.otaku.constant.JwtClaimsConstant;
import com.otaku.context.BaseContext;
import com.otaku.dto.UserDTO;
import com.otaku.dto.UserLoginDTO;
import com.otaku.entity.User;
import com.otaku.properties.JwtProperties;
import com.otaku.result.Result;
import com.otaku.service.CheckinService;
import com.otaku.service.UserService;
import com.otaku.utils.JwtUtil;
import com.otaku.vo.UserLoginVO;
import com.otaku.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户相关接口
 */
@RestController("userUserController")
@RequestMapping("user/user")
@Api(tags = "用户相关接口")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private CheckinService checkinService;

    /**
     * 用户登录
     *
     * @param userLoginDTO 登录时传递的数据模型
     * @return 返回登录成功的结果
     */
    @PostMapping("/login")
    @ApiOperation(value = "用户登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户 {} 正在登录", userLoginDTO);

        User user = userService.login(userLoginDTO);

        //为用户生成专用的令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }

    /**
     * 用户注册
     *
     * @param userDTO 注册时传递的数据
     * @return 返回注册成功的结果
     */
    @PostMapping("/register")
    @ApiOperation(value = "用户注册")
    public Result Register(@RequestBody UserDTO userDTO) {
        log.info("用户 {} 正在注册", userDTO);
        userService.register(userDTO);
        return Result.success();
    }

    /**
     * 用户更新账号信息
     *
     * @param userDTO 注册时传递的数据
     * @return 返回注册成功的结果
     */
    @PostMapping("/update")
    @ApiOperation(value = "用户更新账号信息")
    public Result Update(@RequestBody UserDTO userDTO) {
        // 解析令牌，获取用户ID
        Long userId = BaseContext.getCurrentId();

        // 进行权限校验
        if (!userId.equals(userDTO.getId())) {
            // 如果请求的用户ID与令牌中的用户ID不匹配，拒绝访问
            return Result.error("无权访问该用户数据");
        }
        log.info("用户 {} 更新账号信息", userDTO);
        userService.update(userDTO);
        return Result.success();
    }

    /**
     * 根据ID查询用户
     * 用于数据回显
     *
     * @param id 用户ID
     * @return 返回用户信息
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据ID查询用户")
    public Result<UserVO> getById(@PathVariable Long id) {
        // 解析令牌，获取用户ID
        Long userId = BaseContext.getCurrentId();

        // 进行权限校验
        if (!userId.equals(id)) {
            // 如果请求的用户ID与令牌中的用户ID不匹配，拒绝访问
            return Result.error("无权访问该用户数据");
        }

        log.info("用户 {} 查询账号信息", id);
        UserVO userVO = userService.getById(id);
        return Result.success(userVO);
    }

    /**
     * 用户退出
     *
     * @return 返回退出成功的结果
     */
    @GetMapping("/logout")
    @ApiOperation(value = "用户退出")
    public Result logout() {
        return Result.success();
    }


    /**
     * 用户签到
     *
     * @param userId 用户ID
     * @return 返回签到成功的结果
     */
    @PostMapping("/checkin")
    @ApiOperation(value = "用户签到")
    public Result checkin(@RequestParam("userId") Long userId) {
        checkinService.checkin(userId);
        log.info("用户 {} 已签到", userId);
        return Result.success("已签到");
    }
}
