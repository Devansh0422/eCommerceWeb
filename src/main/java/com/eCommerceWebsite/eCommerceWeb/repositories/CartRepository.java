package com.eCommerceWebsite.eCommerceWeb.repositories;

import com.eCommerceWebsite.eCommerceWeb.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    @Query("SELECT c from Cart c WHERE c.user.email =?1")
    Cart findCartByEmail(String email);

}
