package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
public class AccountForEmployee implements Serializable {
    @Id
    @Column(unique = true, nullable = false)
    private String id;

    @NotBlank(message = "Name can not blank!") //ko cho de trong, neu de trong se hien messsage "Name can not blank!"
    private String name;

    @NotBlank(message = "Username can not blank!")
    @Size(min = 6, message = "Password must be more than 6 characters")
    private String username;

    private String img;

    @Pattern(regexp = "^[\\w.-]+@[\\w-]+\\.[\\w]{2,}$", message = "email is invalid!")
    @NotBlank(message = "email must not blank!")
    private String email;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "phone number is invalid!")
    @NotBlank(message = "phone number must not blank!")
    private String phoneNumber;


    private double experience;


    private double baseSalary;

    private Date createdAt;

    @NotBlank(message = "Password can not blank!")
    @Size(min = 6, message = "Password must be more than 6 characters")
    private String password;

    @NotBlank(message = "role must not blank!")
    @Pattern(regexp = "Manager|Stylist|Staff|Admin", message = "role invalid")
    private String role;


    private String Status;

    boolean isDeleted = false;


}
