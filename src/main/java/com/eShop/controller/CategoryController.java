package com.eShop.controller;


import com.eShop.exceptions.AlreadyExistsException;
import com.eShop.exceptions.ResourceNotFoundException;
import com.eShop.model.Category;
import com.eShop.response.ApiResponse;
import com.eShop.service.category.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/categories")
public class CategoryController {

    private final ICategoryService categoryService;

    private static final String API_PREFIX = "/api";
    private static final String CATEGORIES_PATH = "/all";
    private static final String CATEGORY_PATH = "/category";
    private static final String CATEGORY_ID_PATH = "/{id}";
    private static final String CATEGORY_ADD_PATH = "/add";
    private static final String CATEGORY_UPDATE_PATH = "/update";
    private static final String CATEGORY_DELETE_PATH = "/delete";
    private static final String CATEGORY_COUNT_PATH = "/count";
    private static final String CATEGORY_BY_NAME_PATH = "/productName";
    private static final String CATEGORY_BY_BRAND_PATH = "/byBrand";
    private static final String CATEGORY_BY_NAME_AND_BRAND_PATH = "/byNameAndBrand";
    private static final String CATEGORY_BY_NAME_AND_CATEGORY_PATH = "/byNameAndCategory";
    private static final String CATEGORY_BY_CATEGORY_AND_BRAND_PATH = "/byCategoryAndBrand";
    private static final String CATEGORY_BY_CATEGORY_AND_NAME_PATH = "/byCategoryAndName";
    private static final String CATEGORY_BY_CATEGORY_AND_NAME_AND_BRAND_PATH = "/byCategoryAndNameAndBrand";


    @GetMapping(CATEGORY_PATH + CATEGORY_ID_PATH)
    public ResponseEntity<ApiResponse> getCategoryById(@PathVariable Long id) {
        try {
            Category category = categoryService.getCategoryById(id);
            return ResponseEntity.ok(new ApiResponse("Got category successfully", category));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Failed to get category", INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping(CATEGORIES_PATH)
    public ResponseEntity<ApiResponse> getAllCategories() {
        try {
            List<Category> categories = categoryService.getAllCategories();
            return ResponseEntity.ok(new ApiResponse("Got all categories successfully", categories));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to get all categories", INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping(CATEGORY_PATH + CATEGORY_BY_NAME_PATH)
    public ResponseEntity<ApiResponse> getCategoryByName(@RequestParam String name) {
        try {
            Category category = categoryService.getCategoryByName(name);
            return ResponseEntity.ok(new ApiResponse("Got category by productName successfully", category));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Failed to get category by productName", null));
        }
    }

    @PostMapping(CATEGORY_ADD_PATH)
    public ResponseEntity<ApiResponse> addCategory(@RequestBody Category name) {
        try {
            Category newCategory = categoryService.addCategory(name);
            return ResponseEntity.ok(new ApiResponse("Added category successfully", newCategory));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping(CATEGORY_PATH + CATEGORY_UPDATE_PATH + CATEGORY_ID_PATH)
    public ResponseEntity<ApiResponse> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        try {
            Category updatedCategory = categoryService.updateCategory(category, id);
            return ResponseEntity.ok(new ApiResponse("Updated category successfully", updatedCategory));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Failed to update category", null));
        }
    }

    @DeleteMapping(CATEGORY_PATH + CATEGORY_DELETE_PATH + CATEGORY_ID_PATH)
    public ResponseEntity<ApiResponse> deleteCategoryById(@PathVariable Long id) {
        try {
            Category category = categoryService.getCategoryById(id);
            if (category != null) {
                categoryService.deleteCategoryById(id);
                return ResponseEntity.ok(new ApiResponse("Deleted category successfully", null));
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse( e.getMessage(), null));
        }
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Deletion of category failed", INTERNAL_SERVER_ERROR));
    }

}