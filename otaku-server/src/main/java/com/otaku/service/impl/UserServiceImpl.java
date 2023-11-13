package com.otaku.service.impl;

import com.otaku.constant.MessageConstant;
import com.otaku.dto.UserDTO;
import com.otaku.dto.UserLoginDTO;
import com.otaku.entity.User;
import com.otaku.exception.AccountNotFoundException;
import com.otaku.exception.PasswordErrorException;
import com.otaku.mapper.UserMapper;
import com.otaku.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登录
     * @param userLoginDTO 登录信息
     * @return
     */
    @Override
    public User login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        User user = userMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (user == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //后期需要进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(user.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        //3、返回实体对象
        return user;

    }

    @Override
    public void register(UserDTO userDTO) {
        User user = new User();

        //对象的数据拷贝
        BeanUtils.copyProperties(userDTO, user);
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        user.setCreateTime(LocalDateTime.now());
        userMapper.insert(user);
    }
}
