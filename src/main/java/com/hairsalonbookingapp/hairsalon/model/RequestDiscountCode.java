package com.hairsalonbookingapp.hairsalon.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class RequestDiscountCode {
    @Id
    @Column(unique = true, nullable = false)
    private String discountCodeId;

    private String status;

    private double percentage;

    private String discountProgramId;

    private  String appointmentId;

    private boolean isDeleted = false;
}
