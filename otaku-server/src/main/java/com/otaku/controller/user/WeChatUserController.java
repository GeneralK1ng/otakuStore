package com.otaku.controller.user;


import com.otaku.constant.JwtClaimsConstant;
import com.otaku.dto.UserLoginDTO;
import com.otaku.entity.User;
import com.otaku.properties.JwtProperties;
import com.otaku.result.Result;
import com.otaku.service.WeChatUserService;
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
@RequestMapping("/wxuser/user")
@Api(tags = "微信端用户相关接口")
@Slf4j
public class WeChatUserController {

    @Autowired
    private WeChatUserService weChatUserService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 微信用户登录
     * @param userLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "微信登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("微信用户登录：{}", userLoginDTO.getCode());

        //调用微信登录
        User user = weChatUserService.weChatLogin(userLoginDTO);

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
}
