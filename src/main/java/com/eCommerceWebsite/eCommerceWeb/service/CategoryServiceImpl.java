package com.eCommerceWebsite.eCommerceWeb.service;

import com.eCommerceWebsite.eCommerceWeb.model.Category;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private List<Category> categories = new ArrayList<>();
    private Long nextId = 1L;

    @Override
    public List<Category> getAllCategories() {
        return categories;
    }

    @Override
    public void createCategory(Category category) {
        category.setCategoryId(nextId++);
        categories.add(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categories.stream().filter(c -> c.getCategoryId().equals(categoryId)).findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"resource not found"));
        categories.remove(category);
        return "Category with id "+categoryId+" is deleted";
    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {
        Optional<Category> updateCategory = categories.stream().filter(c -> c.getCategoryId().equals(categoryId)).findFirst();
        if(updateCategory.isPresent()){
            Category existingCategory = updateCategory.get();
            existingCategory.setCategoryName(category.getCategoryName());
            return existingCategory;
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"resource not found");
        }
    }
}
