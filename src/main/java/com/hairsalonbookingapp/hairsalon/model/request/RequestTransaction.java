package com.hairsalonbookingapp.hairsalon.model.request;

import lombok.Data;

@Data
public class RequestTransaction {
    private String transactionType;

    private long money;

    private String description;
}
