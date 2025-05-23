package com.eShop.service.category;

import com.eShop.exceptions.AlreadyExistsException;
import com.eShop.exceptions.ResourceNotFoundException;
import com.eShop.model.Category;

import java.util.List;

public interface ICategoryService {

    Category getCategoryById(Long id) throws ResourceNotFoundException;
    Category getCategoryByName(String name);
    List<Category> getAllCategories();
    Category addCategory(Category category) throws AlreadyExistsException;
    Category updateCategory(Category category, Long id);
    void deleteCategoryById(Long id);

}
