package com.hairsalonbookingapp.hairsalon.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestForEmployee {

    @NotBlank(message = "Name must not blank!")
    String name;

    @NotBlank(message = "Password must not blank!")
    @Size(min = 6, message = "Password must be more than 6 characters")
    String password;
}
