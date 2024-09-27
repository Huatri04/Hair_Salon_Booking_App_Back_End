package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "DiscountCode")
public class DiscountCode {
    @Id
    @Column(unique = true, nullable = false)
    private String discountCodeId;

    private String status;

    private double percentage;

    @ManyToOne
    @JoinColumn(name = "discountProgramId", nullable = false) // day la foreign key
    private DiscountProgram discountProgram;

    @ManyToOne
    @JoinColumn(name = "phoneNumber", nullable = false) // day la foreign key
    private AccountForCustomer customer;

    private  String appointmentId;

    private boolean isDeleted = false;
}
