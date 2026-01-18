package com.eCommerceWebsite.eCommerceWeb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
@ToString
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;

    @NotBlank
    @Size(min = 3, message = "Product name must have 3 character")
    private String productName;
    @NotBlank
    @Size(min = 6, message = "Product description must have 6 character")
    private String description;
    private double price;
    private Double discount;
    private String image;
    private Integer quantity;
    private Double specialPrice;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User user;
}
