package com.eCommerceWebsite.eCommerceWeb.repositories;

import com.eCommerceWebsite.eCommerceWeb.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
