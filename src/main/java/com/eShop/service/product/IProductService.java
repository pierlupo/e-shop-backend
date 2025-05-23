package com.eShop.service.product;

import com.eShop.dto.ProductDto;
import com.eShop.model.Product;
import com.eShop.request.AddProductRequest;
import com.eShop.request.ProductUpdateRequest;

import java.util.List;

public interface IProductService {

    Product addProduct(AddProductRequest request);
    Product getProductById(Long id);
    void deleteProductById(Long id);
    Product updateProduct(ProductUpdateRequest product, Long productId);
    List<Product> getAllProducts();
    List<Product> getProductsByCategory(String category);
    List<Product> getProductsByBrand(String brand);
    List<Product> getProductsByCategoryAndBrand(String category, String brand);
    List<Product> getProductByName(String productName);
    List<Product> getProductByBrandAndName(String brand, String productName);
    Long countProductsByBrandAndName(String brand, String productName);

    List<ProductDto> getConvertedProducts(List<Product> products);

    ProductDto convertToDTO(Product product);
}