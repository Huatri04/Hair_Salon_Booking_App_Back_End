package com.hairsalonbookingapp.hairsalon.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestForEmployee {

    @NotBlank(message = "Name must not blank!") //ko cho de trong, neu de trong se hien messsage "Name can not blank!"
    private String name;

    private String img;

    @Pattern(regexp = "^[\\w.-]+@[\\w-]+\\.[\\w]{2,}$", message = "email is invalid!")
    @NotBlank(message = "email must not blank!")
    private String email;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "phone number is invalid!")
    @NotBlank(message = "phone number must not blank!")
    private String phoneNumber;

    private double experience;

    @NotBlank(message = "Password must not blank!")
    @Size(min = 6, message = "Password must be more than 6 characters")
    private String password;

    @NotBlank(message = "role must not blank!")
    @Pattern(regexp = "Manager|Stylist|Staff|Admin", message = "role invalid")
    private String role;

}
