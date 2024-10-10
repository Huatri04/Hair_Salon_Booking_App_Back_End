package com.hairsalonbookingapp.hairsalon.model.request;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class RequestDiscountCode {
    @Id
    @Column(unique = true, nullable = false)
    private String discountCodeId;

    private  String appointmentId;

}
