package com.hairsalonbookingapp.hairsalon.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class RegisterRequestForEmployee {

    @NotBlank(message = "Username must not be blank!")
    private String username;

    @NotBlank(message = "Name must not be blank!")
    private String name;

    private String img;

    @Pattern(regexp = "^[\\w.-]+@[\\w-]+\\.[\\w]{2,}$", message = "Email is invalid!")
    @NotBlank(message = "Email must not be blank!")
    private String email;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Phone number is invalid!")
    @NotBlank(message = "Phone number must not be blank!")
    private String phoneNumber;

    private String degrees; // Bằng cấp // [Stylist]

    @NotBlank(message = "Password must not be blank!")
    @Size(min = 6, message = "Password must be more than 6 characters")
    private String password;

    @NotBlank(message = "Role must not be blank!")
    @Pattern(regexp = "Manager|Stylist|Staff|Admin", message = "Role is invalid!")
    private String role;

    @Pattern(regexp = "Normal|Expert|NotStylist", message = "StylistLevel is invalid!")
    private String stylistLevel; // [Stylist]

}
