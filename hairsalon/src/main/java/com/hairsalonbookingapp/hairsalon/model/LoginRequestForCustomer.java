package com.hairsalonbookingapp.hairsalon.model;

import lombok.Data;

@Data
public class LoginRequestForCustomer {
    String phoneNumber;
    String password;
}
