package com.hairsalonbookingapp.hairsalon.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestForCustomer {

    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Phone number is invalid!")
    //@NotBlank(message = "Phone number must not be blank!")
    String phoneNumber;

    //@NotBlank(message = "Password must not blank!")
    //@Size(min = 6, message = "Password must be more than 6 characters")
    String password;
}
