package com.eCommerceWebsite.eCommerceWeb.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private Long categoryId;

    @NotBlank
    @Size(min = 5, message = "size must be greater than 5 or equal to")
    private String categoryName;

}
