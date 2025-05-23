package com.eShop.repository;

import com.eShop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT DISTINCT p FROM Product p WHERE p.category.name = :categoryName")
    List<Product> findByCategoryName(String categoryName);

    @Query("SELECT DISTINCT p FROM Product p WHERE p.brand = :brand")
    List<Product> findBybrand(String brand);

    @Query("SELECT DISTINCT p FROM Product p WHERE p.category.name = :category AND p.brand = :brand")
    List<Product> findByCategoryNameAndbrand(String category, String brand);

    @Query("SELECT DISTINCT p FROM Product p WHERE p.productName = :productName")
    List<Product> findByProductName(String productName);

    @Query("SELECT DISTINCT p FROM Product p WHERE p.productName = :productName AND p.brand = :brand")
    List<Product> findBybrandAndProductName(String brand, String productName);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.productName = :productName AND p.brand = :brand")
    Long countBybrandAndProductName(String brand, String productName);

    @Query("SELECT DISTINCT p FROM Product p WHERE p.productName = :productName AND p.category.name = :category")
    List<Product> findByCategoryNameAndProductName(String category, String productName);

    @Query("SELECT DISTINCT p FROM Product p WHERE p.productName = :productName AND p.category.name = :category AND p.brand = :brand")
    List<Product> findByCategoryNameAndbrandAndProductName(String category, String brand, String productName);

    @Query("SELECT DISTINCT p FROM Product p WHERE p.productName = :productName AND p.category.name = :category AND p.brand = :brand AND p.inventory > 0")
    List<Product> findByCategoryNameAndbrandAndProductNameAndInventoryGreaterThanZero(String category, String brand, String productName);

    @Query("SELECT DISTINCT p FROM Product p WHERE p.productName = :productName AND p.category.name = :category AND p.brand = :brand AND p.inventory > 0")
    Long countByCategoryNameAndbrandAndProductNameAndInventoryGreaterThanZero(String category, String brand, String productName);

    @Query("SELECT DISTINCT p FROM Product p WHERE p.productName = :productName AND p.category.name = :category AND p.brand = :brand AND p.inventory > 0")
    List<Product> findByCategoryNameAndbrandAndProductNameAndInventoryGreaterThanZero(String category, String brand, String productName, int page, int size);

    boolean existsByProductNameAndBrand(String name, String brand);
}