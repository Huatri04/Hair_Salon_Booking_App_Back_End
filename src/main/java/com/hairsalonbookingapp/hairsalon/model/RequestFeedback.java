package com.hairsalonbookingapp.hairsalon.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class RequestFeedback {
    @Id
    @Column(unique = true, nullable = false)
    private String feedbackId;

    @Min(value = 1, message = "start must at least 1")
    @Max(value = 5, message = "start must smaller than 5")
    private int start;

    private String comment;

    private String phoneNumberOfCustomer;
}
