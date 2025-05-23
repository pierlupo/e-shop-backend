package com.eShop.dto;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemDto {

    private Long itemId;

    private int quantity;

    private BigDecimal unitPrice;

    private ProductDto product;

}