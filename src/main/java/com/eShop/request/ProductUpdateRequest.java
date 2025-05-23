package com.eShop.request;

import com.eShop.model.Category;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateRequest {

    private Long id;
    private String productName;
    private String brand;
    private BigDecimal price;
    private int inventory;
    private String description;
    private Category category;

}
