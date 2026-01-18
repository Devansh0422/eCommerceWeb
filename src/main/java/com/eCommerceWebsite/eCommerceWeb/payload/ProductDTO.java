package com.eCommerceWebsite.eCommerceWeb.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private Long productId;
    private String productName;
    private String description;
    private String image;
    private double price;
    private Integer quantity;
    private Double discount;
    private  Double specialPrice;

}
