package com.dailycodework.dreamshops.service.category;

import com.dailycodework.dreamshops.exceptions.AlreadyExistsException;
import com.dailycodework.dreamshops.exceptions.ResourceNotFoundException;
import com.dailycodework.dreamshops.model.Category;

import java.util.List;

public interface ICategoryService {

    Category getCategoryById(Long id) throws ResourceNotFoundException;
    Category getCategoryByName(String name);
    List<Category> getAllCategories();
    Category addCategory(Category category) throws AlreadyExistsException;
    Category updateCategory(Category category, Long id);
    void deleteCategoryById(Long id);

}
