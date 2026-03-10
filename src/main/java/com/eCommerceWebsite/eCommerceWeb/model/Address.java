package com.eCommerceWebsite.eCommerceWeb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
@AllArgsConstructor
@Table(name="addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min = 5, message = "Street name must have least 5 character")
    private String street;

    @NotBlank
    @Size(min = 5, message = "Building name must have least 5 character")
    private String buildingName;

    @NotBlank
    @Size(min = 4, message = "City name must have least 4 character")
    private String city;

    @NotBlank
    @Size(min = 2, message = "State name must have least 2 character")
    private String state;

    @NotBlank
    @Size(min = 2, message = "Country name must have least 2 character")
    private String country;

    @NotBlank
    @Size(min = 6, max = 6, message = "Pincode must have 6 character")
    private Integer pincode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user ;

    public Address(String street, String buildingName, String city, String state, String country, Integer pincode) {
        this.street = street;
        this.buildingName = buildingName;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pincode = pincode;
    }
}
