package com.hairsalonbookingapp.hairsalon.model;

import lombok.Data;

@Data
public class DiscountCodeResponse {
    private String discountCodeId;

    private String status;

    private double percentage;

    private String discountProgramId;

    private  String appointmentId;
}
