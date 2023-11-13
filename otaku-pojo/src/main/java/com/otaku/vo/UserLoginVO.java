package com.otaku.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginVO implements Serializable {
    /**
     * 私有属性：id
     * 类型：Long
     * 说明：实体的唯一标识符
     */
    private Long id;

    /**
     * 私有属性：openid
     * 类型：String
     * 说明：用户在第三方平台的唯一标识符
     */
    private String openid;

    /**
     * 私有属性：username
     * 类型：String
     * 说明：用户的用户名
     */
    private String username;

    /**
     * 私有属性：token
     * 类型：String
     * 说明：用于验证用户身份的令牌
     */
    private String token;


}
