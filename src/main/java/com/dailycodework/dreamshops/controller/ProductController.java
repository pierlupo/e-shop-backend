package com.dailycodework.dreamshops.controller;

import com.dailycodework.dreamshops.dto.ProductDto;
import com.dailycodework.dreamshops.exceptions.AlreadyExistsException;
import com.dailycodework.dreamshops.model.Product;
import com.dailycodework.dreamshops.request.AddProductRequest;
import com.dailycodework.dreamshops.request.ProductUpdateRequest;
import com.dailycodework.dreamshops.response.ApiResponse;
import com.dailycodework.dreamshops.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}")
public class ProductController {

    private final IProductService productService;

    private static final String ALL_PRODUCTS_PATH = "/all";
    private static final String PRODUCT_PATH = "/product";
    private static final String PRODUCTS_PATH = "/products";
    private static final String PRODUCT_ID_PATH = "/{productId}";
    private static final String PRODUCT_ADD_PATH = "/add";
    private static final String PRODUCT_UPDATE_PATH = "/update";
    private static final String PRODUCT_DELETE_PATH = "/delete";
    private static final String PRODUCT_COUNT_PATH = "/count";
    private static final String PRODUCTS_BY_PRODUCT_NAME_PATH = "/by-product-name";
    private static final String PRODUCTS_BY_BRAND_NAME_PATH = "/by-brand";
    private static final String PRODUCTS_BY_CATEGORY_PATH = "/{category}";
    private static final String PRODUCTS_BY_CATEGORY_AND_BRAND_PATH = "/by-category-and-brand";


    @GetMapping(PRODUCT_PATH + PRODUCT_ID_PATH)
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long productId) {
        try {
            Product product = productService.getProductById(productId);
            ProductDto productDTO = productService.convertToDTO(product);
            return ResponseEntity.ok(new ApiResponse("Got product successfully", productDTO));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping(PRODUCTS_PATH + ALL_PRODUCTS_PATH)
    public ResponseEntity<ApiResponse> getAllProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
            return ResponseEntity.ok(new ApiResponse("Got all products successfully", convertedProducts));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), INTERNAL_SERVER_ERROR));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(PRODUCTS_PATH + PRODUCT_ADD_PATH)
    public ResponseEntity<ApiResponse> addProduct(@RequestBody AddProductRequest product) {
        try {
            Product newProduct = productService.addProduct(product);
            ProductDto productDTO = productService.convertToDTO(newProduct);
            return ResponseEntity.ok(new ApiResponse("Added product successfully", productDTO));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(PRODUCT_PATH + PRODUCT_ID_PATH + PRODUCT_UPDATE_PATH)
    public ResponseEntity<ApiResponse> updateProduct(@RequestBody ProductUpdateRequest request, @PathVariable Long productId) {
        try {
            Product updatedProduct = productService.updateProduct(request, productId);
            ProductDto productDTO = productService.convertToDTO(updatedProduct);
            return ResponseEntity.ok(new ApiResponse("Updated product successfully", productDTO));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(PRODUCT_PATH + PRODUCT_ID_PATH + PRODUCT_DELETE_PATH)
    public ResponseEntity<ApiResponse> deleteProductById(@PathVariable Long productId) {
        try {
            Product product = productService.getProductById(productId);
            if (product != null) {
                productService.deleteProductById(productId);
                return ResponseEntity.ok(new ApiResponse("Deleted product successfully", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse( e.getMessage(), null));
        }
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Deletion of product failed", INTERNAL_SERVER_ERROR));
    }

    @GetMapping(PRODUCTS_PATH + PRODUCTS_BY_PRODUCT_NAME_PATH)
    public ResponseEntity<ApiResponse> getProductsByName(@RequestParam String productName) {
        try {
            List<Product> products = productService.getProductByName(productName);
            if(products.isEmpty()) {
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No product found", null));
            }
            List<ProductDto> productDtos = productService.getConvertedProducts(products);
            return ResponseEntity.ok(new ApiResponse("Got product by productName successfully", productDtos));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Failed to get product by productName", null));
        }
    }

    @GetMapping(PRODUCTS_PATH + PRODUCTS_BY_BRAND_NAME_PATH)
    public ResponseEntity<ApiResponse> getProductsByBrand(@RequestParam String brand) {
        try {
            List<Product> products = productService.getProductsByBrand(brand);
            if(products.isEmpty()) {
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No product found", null));
            }
            List<ProductDto> productDtos = productService.getConvertedProducts(products);
            return ResponseEntity.ok(new ApiResponse("Got product(s) by brand name successfully", productDtos));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Failed to get product by brand name", null));
        }
    }

    @GetMapping(PRODUCTS_PATH + PRODUCTS_BY_CATEGORY_PATH + ALL_PRODUCTS_PATH)
    public ResponseEntity<ApiResponse> getProductsByCategory(@PathVariable String category) {
        try {
            List<Product> products = productService.getProductsByCategory(category);
            if(products.isEmpty()) {
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No products found", null));
            }
            List<ProductDto> productDtos = productService.getConvertedProducts(products);
            return ResponseEntity.ok(new ApiResponse("Got products by category successfully", productDtos));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Failed to get products by category", null));
        }
    }

    @GetMapping(PRODUCTS_PATH + PRODUCTS_BY_BRAND_NAME_PATH + PRODUCTS_BY_PRODUCT_NAME_PATH)
    public ResponseEntity<ApiResponse> getProductsByBrandAndName(@RequestParam String brand, @RequestParam String productName) {
        try {
            List<Product> products = productService.getProductByBrandAndName(brand, productName);
            if(products.isEmpty()) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No product found", null));
        }
            List<ProductDto> productDtos = productService.getConvertedProducts(products);
            return ResponseEntity.ok(new ApiResponse("Got product by name and brand successfully", productDtos));
    } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Failed to get product by name and brand", null));
        }
    }

    @GetMapping(PRODUCTS_PATH + PRODUCTS_BY_CATEGORY_AND_BRAND_PATH)
    public ResponseEntity<ApiResponse> getProductsByCategoryAndBrand(@RequestParam String category,@RequestParam String brand) {
        try {
            List<Product> products = productService.getProductsByCategoryAndBrand(category, brand);
            if(products.isEmpty()) {
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No product found", null));
            }
            List<ProductDto> productDtos = productService.getConvertedProducts(products);
            return ResponseEntity.ok(new ApiResponse("Got product by category and brand successfully", productDtos));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Failed to get product by category and brand", null));
        }
    }

    @GetMapping(PRODUCTS_PATH + PRODUCT_COUNT_PATH + PRODUCTS_BY_BRAND_NAME_PATH + PRODUCTS_BY_PRODUCT_NAME_PATH)
    public ResponseEntity<ApiResponse> countProductsByBrandAndName(@RequestParam String brand, @RequestParam String productName) {
        try {
            var productCount = productService.countProductsByBrandAndName(brand, productName);
            return ResponseEntity.ok(new ApiResponse("Counted products by name and brand successfully", productCount));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Failed to count products by name and brand", null));
        }
    }
}