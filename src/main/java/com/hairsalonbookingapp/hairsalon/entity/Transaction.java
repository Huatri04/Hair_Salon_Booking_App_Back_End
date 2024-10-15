package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "Transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int transactionId;

    private String transactionType;

    private long money;

    private String description;

    private Date date;

    @ManyToOne
    @JoinColumn(name = "employeeId", nullable = false) // day la foreign key
    private AccountForEmployee employee;

    @ManyToOne
    @JoinColumn(name = "phoneNumber", nullable = false) // day la foreign key
    private AccountForCustomer customer;

    @ManyToOne
    @JoinColumn(name = "paymentId")
    private Payment payment;


    private boolean isDeleted = false;
}