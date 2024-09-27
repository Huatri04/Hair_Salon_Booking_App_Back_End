package com.hairsalonbookingapp.hairsalon.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Data
public class AppointmentResponse {
    private String appointmentId;

    private Date date;

    private int slot;

    private String status;

    private String serviceStylistSupport;

    private boolean isDeleted;
}
