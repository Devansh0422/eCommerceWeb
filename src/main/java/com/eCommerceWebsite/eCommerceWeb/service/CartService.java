package com.eCommerceWebsite.eCommerceWeb.service;

import com.eCommerceWebsite.eCommerceWeb.payload.CartDTO;

public interface CartService {

    CartDTO addProductToCart(Long productId, Integer quantity);
}
