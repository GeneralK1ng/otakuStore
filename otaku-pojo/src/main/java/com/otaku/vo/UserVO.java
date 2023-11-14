package com.otaku.vo;

import lombok.Data;

@Data
public class UserVO {
    //主键
    private Long id;

    //用户名
    private String username;

    //密码
    private String password;

    //微信用户唯一标识
    private String openid;

    //姓名
    private String name;

    //手机号
    private String phone;

    //性别 0 女 1 男
    private String gender;

    //身份证号
    private String idNumber;

    //头像
    private String avatar;
}
