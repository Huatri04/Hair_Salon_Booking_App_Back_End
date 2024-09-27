package com.hairsalonbookingapp.hairsalon.model;

import lombok.Data;

@Data
public class AccountForCustomerResponse {
    private String email;
    private String name;

    private String phoneNumber;
    private String token;
    private long score;
}
