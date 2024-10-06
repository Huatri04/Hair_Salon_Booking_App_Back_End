package com.hairsalonbookingapp.hairsalon.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Data
public class RequestDiscountprogram {

    private int discountProgramId;

    private String name;

    private String description;

    private Date startedDate;

    private Date endedDate;

    private long amount;

    private String status;

    private double percentage;

}