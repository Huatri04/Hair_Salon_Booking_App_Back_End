package com.hairsalonbookingapp.hairsalon.model.response;

import lombok.Data;

@Data
public class CustomerAccountInfo {
    String customerName;
    String phoneNumber;
    boolean isDeleted;
}
