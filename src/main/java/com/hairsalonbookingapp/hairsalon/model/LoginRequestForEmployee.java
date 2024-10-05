package com.hairsalonbookingapp.hairsalon.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestForEmployee {

    @NotBlank(message = "Username must not be blank!")
    @Pattern(regexp = "^(?!84|0[3-9][0-9]{8}$).*$", message = "Username is invalid!")
    String username;

    String password;
}
