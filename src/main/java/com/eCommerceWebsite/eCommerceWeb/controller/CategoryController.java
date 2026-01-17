package com.eCommerceWebsite.eCommerceWeb.controller;

import com.eCommerceWebsite.eCommerceWeb.model.Category;
import com.eCommerceWebsite.eCommerceWeb.payload.CategoryDTO;
import com.eCommerceWebsite.eCommerceWeb.payload.CategoryResponse;
import com.eCommerceWebsite.eCommerceWeb.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories() {
        return new ResponseEntity<>(categoryService.getAllCategories(),HttpStatus.OK);
    }

    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO category) {
        CategoryDTO createdCategory = categoryService.createCategory(category);
        return new ResponseEntity<>(createdCategory,HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
        try{
            return  new ResponseEntity<>(categoryService.deleteCategory(categoryId),HttpStatus.OK);
        }catch (ResponseStatusException e){
            return new ResponseEntity<>(e.getReason(),e.getStatusCode());
        }
    }

    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<String> updateCategory(@Valid @RequestBody Category category, @PathVariable Long categoryId) {
        try{
            Category savedCategory = categoryService.updateCategory(category, categoryId);
            return new ResponseEntity<>("Category with id " +categoryId+" update Successfully "+category,HttpStatus.CREATED);
        }catch (ResponseStatusException e){
            return new ResponseEntity<>(e.getReason(),e.getStatusCode());
        }
    }

}