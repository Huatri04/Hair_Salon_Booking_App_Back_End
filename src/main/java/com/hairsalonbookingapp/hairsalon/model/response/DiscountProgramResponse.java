package com.hairsalonbookingapp.hairsalon.model.response;

import com.hairsalonbookingapp.hairsalon.entity.DiscountCode;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class DiscountProgramResponse {

    private int discountProgramId;

    private String name;

    private String description;

    private Date startedDate;

    private Date endedDate;

    private long amount;

    private String status;

    private double percentage;

    private List<DiscountCode> discountCodes;

    private boolean isDeleted = false;
}
