package com.hairsalonbookingapp.hairsalon.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestForEmployee {

    @NotBlank(message = "Name must not blank!")
    @Pattern(regexp = "^[^0-9]+$", message = "Name is invalid!")
    String name;

    String password;
}
