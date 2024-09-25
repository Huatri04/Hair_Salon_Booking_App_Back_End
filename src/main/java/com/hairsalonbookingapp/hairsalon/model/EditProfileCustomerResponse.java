package com.hairsalonbookingapp.hairsalon.model;

import lombok.Data;

@Data
public class EditProfileCustomerResponse {
    private String phoneNumber;
    private String email;
    private String name;
    private long score;
}
