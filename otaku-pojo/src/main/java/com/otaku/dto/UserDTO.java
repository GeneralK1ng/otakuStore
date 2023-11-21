package com.otaku.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDTO implements Serializable {
    //主键
    private Long id;
    //用户名
    private String username;
    //密码
    private String password;
    //姓名
    private String name;
    //手机号
    private String phone;
    //性别
    private String gender;
    //身份证
    private String idNumber;
    //头像
    private String avatar;
}
