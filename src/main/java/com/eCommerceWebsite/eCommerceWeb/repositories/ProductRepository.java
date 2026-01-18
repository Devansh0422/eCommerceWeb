package com.eCommerceWebsite.eCommerceWeb.repositories;

import com.eCommerceWebsite.eCommerceWeb.model.Category;
import com.eCommerceWebsite.eCommerceWeb.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByCategoryOrderByPriceAsc(Pageable pageable, Category category);

    Page<Product> findByProductNameLikeIgnoreCase(Pageable pageable, String keyword);
}
