package com.otaku.controller.user;


import com.otaku.constant.JwtClaimsConstant;
import com.otaku.dto.UserDTO;
import com.otaku.dto.UserLoginDTO;
import com.otaku.entity.User;
import com.otaku.properties.JwtProperties;
import com.otaku.result.Result;
import com.otaku.service.UserService;
import com.otaku.utils.JwtUtil;
import com.otaku.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("user/user")
@Api(tags = "用户相关接口")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;

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
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
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
     * @param userDTO 注册时传递的数据模型
     * @return 返回注册成功的结果
     */
    @PostMapping("/register")
    @ApiOperation(value = "用户注册")
    public Result Register(@RequestBody UserDTO userDTO) {
        log.info("用户 {} 正在注册", userDTO);
        userService.register(userDTO);
        return Result.success();
    }

    @PostMapping("/update")
    @ApiOperation(value = "用户更新账号信息")
    public Result Update(@RequestBody UserDTO userDTO) {
        log.info("用户 {} 更新账号信息", userDTO);
        userService.update(userDTO);
        return Result.success();
    }
}
