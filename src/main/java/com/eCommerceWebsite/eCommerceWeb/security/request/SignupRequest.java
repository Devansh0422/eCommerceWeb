package com.eCommerceWebsite.eCommerceWeb.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class SignupRequest {

    @Email
    @NotBlank
    @Size( max = 20)
    private String email;

    @NotBlank
    @Size( min=8, max = 120)
    private String password;

    @NotBlank
    @Size(min = 5, max = 20)
    private String username;

    private Set<String> role;
}
