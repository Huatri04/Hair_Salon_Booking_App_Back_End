package com.hairsalonbookingapp.hairsalon.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestEditProfileCustomer {
    @Id
    @Column(unique = true)
    private String phoneNumber;

    @Email(message = "Email invalid!")
    private String email;

    private String name;

//    private String password;
}
