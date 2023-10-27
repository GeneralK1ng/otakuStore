package com.otaku.service;

import com.otaku.dto.UserLoginDTO;
import com.otaku.entity.User;

public interface WeChatUserService {
    /**
     * 微信用户登录
     * @param userLoginDTO
     * @return
     */
    User weChatLogin(UserLoginDTO userLoginDTO);
}
