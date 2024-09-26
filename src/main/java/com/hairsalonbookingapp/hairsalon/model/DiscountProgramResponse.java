package com.hairsalonbookingapp.hairsalon.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Data
public class DiscountProgramResponse {

    private String discountProgramId;

    private Date startedDate;

    private Date endedDate;

    private long Amount;

    private String status;

    private double percentage;

    private String discountCodeId;

    private boolean isDeleted = false;
}
