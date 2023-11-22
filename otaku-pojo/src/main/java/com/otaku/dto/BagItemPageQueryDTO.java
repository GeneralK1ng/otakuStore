package com.otaku.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BagItemPageQueryDTO implements Serializable {

    private int page;

    private int pageSize;

    private String name;

}
