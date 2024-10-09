package com.hairsalonbookingapp.hairsalon.model;

import lombok.Data;

import java.util.Date;

@Data
public class RequestTransaction {
    private String transactionType;

    private long money;

    private String description;
}
