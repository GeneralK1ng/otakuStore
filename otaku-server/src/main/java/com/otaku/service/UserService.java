package com.otaku.service;

import com.otaku.dto.UserDTO;
import com.otaku.dto.UserLoginDTO;
import com.otaku.entity.User;
import com.otaku.vo.UserVO;

public interface UserService {
    /**
     * 用户登录
     * @param userLoginDTO 用户登录信息
     * @return
     */
    User login(UserLoginDTO userLoginDTO);

    /**
     * 用户注册
     * @param userDTO 用户注册信息
     */
    void register(UserDTO userDTO);

    /**
     * 更新用户信息
     * @param userDTO 用户信息
     */
    void update(UserDTO userDTO);

    /**
     * 根据ID查询用户信息
     * @param id 用户ID
     * @return 返回查询到的用户信息
     */
    UserVO getById(Long id);
}
