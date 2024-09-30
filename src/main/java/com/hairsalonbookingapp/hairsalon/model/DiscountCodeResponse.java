package com.hairsalonbookingapp.hairsalon.model;

import com.hairsalonbookingapp.hairsalon.entity.AccountForCustomer;
import com.hairsalonbookingapp.hairsalon.entity.DiscountProgram;
import lombok.Data;

@Data
public class DiscountCodeResponse {
    private String discountCodeId;

    private String code;

    private DiscountProgram discountProgram;

    private AccountForCustomer customer;

    private  String appointmentId;
}
