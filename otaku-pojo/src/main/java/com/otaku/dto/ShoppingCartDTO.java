package com.otaku.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class ShoppingCartDTO implements Serializable {

    private Long productId;
    private Long packageId;
    private String productFlavor;

}
