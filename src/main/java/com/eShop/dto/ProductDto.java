package com.eShop.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDto {

    private Long productId;

    private String productName;

    private String brand;

    private BigDecimal price;

    private int inventory;

    private String description;

    private CategoryDto category;

    private List<ImageDto> images;

}