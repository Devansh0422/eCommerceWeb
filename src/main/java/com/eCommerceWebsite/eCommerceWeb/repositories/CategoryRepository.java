package com.eCommerceWebsite.eCommerceWeb.repositories;

import com.eCommerceWebsite.eCommerceWeb.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByCategoryName(String categoryName);
}
