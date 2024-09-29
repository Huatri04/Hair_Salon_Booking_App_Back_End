package com.hairsalonbookingapp.hairsalon.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestForEmployee {

    @NotBlank(message = "Username must not be blank!")
    @Pattern(regexp = "^[^0-9]+$", message = "Username is invalid!")
    String Username;

    String password;
}
