package com.otaku.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class UserPointRewardDTO implements Serializable {

    private Long userId;

    private Integer point;

    private static final Integer type = 1;
}
