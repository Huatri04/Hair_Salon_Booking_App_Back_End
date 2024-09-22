package com.hairsalonbookingapp.hairsalon.model;

import lombok.Data;

@Data
public class AccountForEmployeeResponse {
    private String id;
    private String name;
    private String username;
    private String img;
    private String email;
    private String phoneNumber;
    private String role;
}
