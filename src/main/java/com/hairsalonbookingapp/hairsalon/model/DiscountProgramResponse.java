package com.hairsalonbookingapp.hairsalon.model;

import com.hairsalonbookingapp.hairsalon.entity.DiscountCode;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class DiscountProgramResponse {

    private String discountProgramId;

    private Date startedDate;

    private Date endedDate;

    private long Amount;

    private String status;

    private double percentage;

    private List<DiscountCode> discountCodes;

    private boolean isDeleted = false;
}
