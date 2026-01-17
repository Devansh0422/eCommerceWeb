package com.eCommerceWebsite.eCommerceWeb.service;

import com.eCommerceWebsite.eCommerceWeb.model.Product;
import com.eCommerceWebsite.eCommerceWeb.payload.ProductDTO;
import com.eCommerceWebsite.eCommerceWeb.payload.ProductResponse;

public interface ProductService {
    ProductDTO addProduct(Long categoryId, Product product);

    ProductResponse getAllProducts();

    ProductResponse searchByCategory(Long categoryId);
}
