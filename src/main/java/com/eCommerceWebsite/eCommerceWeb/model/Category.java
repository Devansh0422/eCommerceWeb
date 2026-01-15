package com.eCommerceWebsite.eCommerceWeb.model;

public class Category {

    private String categoryName;
    private Long categoryId;

    public Category( Long categoryId, String categoryName) {
        this.categoryName = categoryName;
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryName='" + categoryName  +
                '}';
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
