package com.eShop.request;

import com.eShop.model.Category;
import lombok.Data;


import java.math.BigDecimal;

//@Data peut être utilisée ici car ce n'est pas une entité dans ma bdd
@Data
public class AddProductRequest {

    private Long id;
    private String productName;
    private String brand;
    private BigDecimal price;
    private int inventory;
    private String description;
    private Category category;

}
