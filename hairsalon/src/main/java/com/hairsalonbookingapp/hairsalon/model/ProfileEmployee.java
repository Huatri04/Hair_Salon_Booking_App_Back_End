package com.hairsalonbookingapp.hairsalon.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
public class ProfileEmployee {
    String id;
    String name;
    String img;
    String email;
    String phoneNumber;
    double experience;
    Date createdAt;
    String password;
    String role;
}
