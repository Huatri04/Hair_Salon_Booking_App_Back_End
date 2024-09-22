package com.hairsalonbookingapp.hairsalon.model;

import lombok.Data;

@Data
public class AccountForCustomerResponse {
    private String email;
    private String name;
    private long score;
    private String phoneNumber;
}
