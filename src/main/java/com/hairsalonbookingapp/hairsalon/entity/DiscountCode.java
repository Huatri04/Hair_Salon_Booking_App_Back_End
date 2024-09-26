package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    private String discountProgramId;

    private  String appointmentId;

    private boolean isDeleted = false;
}
