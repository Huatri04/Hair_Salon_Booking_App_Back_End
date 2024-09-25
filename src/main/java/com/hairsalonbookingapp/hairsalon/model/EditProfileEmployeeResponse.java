package com.hairsalonbookingapp.hairsalon.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;

@Data
public class EditProfileEmployeeResponse {

    private String id;

    private String name;

    private String img;

    private String email;

    private String phoneNumber;

    private String password;
}
