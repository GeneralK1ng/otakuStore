package com.otaku.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.otaku.constant.MessageConstant;
import com.otaku.dto.UserLoginDTO;
import com.otaku.entity.User;
import com.otaku.exception.LoginFailedException;
import com.otaku.mapper.UserMapper;
import com.otaku.properties.WeChatProperties;
import com.otaku.service.WeChatUserService;
import com.otaku.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;


@Service
@Slf4j
public class WeChatUserServiceImpl implements WeChatUserService {

    //微信服务接口地址
    public static final String WECHAT_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;
    /**
     * 用户微信登录
     * @param userLoginDTO
     * @return
     */
    @Override
    public User weChatLogin(UserLoginDTO userLoginDTO) {
        //调用微信接口获取openid
        String openid = getOpenid(userLoginDTO.getCode());

        //判断openId是否为空，如果为空，则表示登录失败，抛出异常
        if(openid.isEmpty()){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //判断当前微信用户是不是一个新用户
        User user = userMapper.getByOpenId(openid);

        //如果是新用户，自动完成注册
        if(user == null){
             user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
             userMapper.insert(user);
        }

        //返回这个对象
        return user;
    }

    /**
     * 调用微信接口，获取微信用户的Openid
     * @param code
     * @return
     */
    private String getOpenid(String code){
        //调用微信接口服务，获得当前用户的openId
        HashMap<String, String > map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");

        String json = HttpClientUtil.doGet(WECHAT_LOGIN, map);

        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");

        return openid;
    }
}
