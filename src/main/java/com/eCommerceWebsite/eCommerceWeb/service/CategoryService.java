package com.eCommerceWebsite.eCommerceWeb.service;

import com.eCommerceWebsite.eCommerceWeb.model.Category;
import com.eCommerceWebsite.eCommerceWeb.payload.CategoryDTO;
import com.eCommerceWebsite.eCommerceWeb.payload.CategoryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CategoryService {

    CategoryResponse getAllCategories();
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    String deleteCategory(Long categoryId);

    Category updateCategory(Category category, Long categoryId);
}
