package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Feedback")
public class Feedback {
    @Id
    @Column(unique = true, nullable = false)
    private String feedbackId;

    @Min(value = 1, message = "start must at least 1")
    @Max(value = 5, message = "start must smaller than 5")
    private int start;

    private String comment;

    private String phoneNumberOfCustomer;

    private boolean isDeleted = false;
}
