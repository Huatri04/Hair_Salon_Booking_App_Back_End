package com.hairsalonbookingapp.hairsalon.model;

import com.hairsalonbookingapp.hairsalon.entity.AccountForEmployee;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.util.Date;

@Data
public class TransactionResponse {
    private int transactionId;

    private String transactionType;

    private long money;

    private String description;

    private Date date;

    @ManyToOne
    @JoinColumn(name = "employeeId", nullable = false) // day la foreign key
    private AccountForEmployee employee;

    private boolean isDeleted;
}
