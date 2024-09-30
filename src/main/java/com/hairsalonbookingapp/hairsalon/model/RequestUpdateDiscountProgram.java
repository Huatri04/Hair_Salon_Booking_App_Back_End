package com.hairsalonbookingapp.hairsalon.model;

import lombok.Data;

import java.util.Date;

@Data
public class RequestUpdateDiscountProgram {
    private int discountProgramId;

    private String name;

    private String description;

    private Date startedDate;

    private Date endedDate;

    private long amount;

    private String status;

    private double percentage;
}
