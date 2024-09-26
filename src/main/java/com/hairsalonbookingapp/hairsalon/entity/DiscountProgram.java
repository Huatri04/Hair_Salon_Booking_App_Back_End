package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "DiscountProgram")
public class DiscountProgram {
    @Id
    @Column(unique = true, nullable = false)
    private String discountProgramId;

    private Date startedDate;

    private Date endedDate;

    private long Amount;

    private String status;

    private double percentage;

    private String discountCodeId;

    private boolean isDeleted = false;
}
