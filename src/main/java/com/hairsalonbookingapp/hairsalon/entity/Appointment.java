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
@Table(name = "Appointment")
public class Appointment {
    @Id
    @Column(unique = true, nullable = false)
    private String appointmentId;

    private Date date;

    private int slot;

    private String status;

    private String serviceStylistSupport;

    private boolean isDeleted = false;

    private String discountCode;

    private String Customer;
}
