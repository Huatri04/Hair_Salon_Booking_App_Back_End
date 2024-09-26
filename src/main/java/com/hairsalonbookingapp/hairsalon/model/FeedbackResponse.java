package com.hairsalonbookingapp.hairsalon.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class FeedbackResponse {

    private String feedbackId;

    private int start;

    private String comment;

    private String phoneNumberOfCustomer;
}
