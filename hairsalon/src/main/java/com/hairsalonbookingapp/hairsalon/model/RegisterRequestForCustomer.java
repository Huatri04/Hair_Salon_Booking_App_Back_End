package com.hairsalonbookingapp.hairsalon.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
public class RegisterRequestForCustomer {
    @Pattern(regexp = "^[\\w.-]+@[\\w-]+\\.[\\w]{2,}$", message = "Email is invalid!")
    @NotBlank(message = "Email must not be blank!")
    private String email;

    @NotBlank(message = "Username must not be blank!")
    @Size(min = 3, message = "Username must be more than 3 characters!")
    private String username;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Phone number is invalid!")
    @NotBlank(message = "Phone number must not be blank!")
    private String phoneNumber;

    @NotBlank(message = "Password must not be blank!")
    @Size(min = 6, message = "Password must be more than 6 characters")
    private String password;
}
