package com.eCommerceWebsite.eCommerceWeb.service;

import com.eCommerceWebsite.eCommerceWeb.exceptions.APIException;
import com.eCommerceWebsite.eCommerceWeb.exceptions.ResourceNotFoundException;
import com.eCommerceWebsite.eCommerceWeb.model.Category;
import com.eCommerceWebsite.eCommerceWeb.payload.CategoryDTO;
import com.eCommerceWebsite.eCommerceWeb.payload.CategoryResponse;
import com.eCommerceWebsite.eCommerceWeb.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

   // private List<Category> categories = new ArrayList<>();
   // private Long nextId = 1L;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories() {

        if(categoryRepository.findAll().isEmpty()) {
            throw new APIException("Category List is Empty");
        }
        List<CategoryDTO> categoryDTOList = categoryRepository.findAll().stream().map(category -> modelMapper.map(category, CategoryDTO.class)).toList();

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOList);
        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        if (savedCategory != null) {
            throw new APIException("Category already exists with the name: " + category.getCategoryName());
        }
      //  category.setCategoryId(nextId++);
        return modelMapper.map(categoryRepository.save(category), CategoryDTO.class);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException( "Category","categoryId", categoryId));
        categoryRepository.delete(category);
        return "Category with id "+categoryId+" is deleted";
    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {
        Category savedCategory = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException( "Category","categoryId", categoryId));
        category.setCategoryId(categoryId);
        savedCategory = categoryRepository.save(savedCategory);
        return savedCategory;
    }
}
