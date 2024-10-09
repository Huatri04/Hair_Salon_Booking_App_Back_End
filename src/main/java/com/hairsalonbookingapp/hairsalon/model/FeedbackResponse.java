package com.hairsalonbookingapp.hairsalon.model;

import com.hairsalonbookingapp.hairsalon.entity.AccountForCustomer;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.Date;

@Data
public class FeedbackResponse {

    private int feedbackId;

    private int star;

    private String comment;

    private AccountForCustomer customer;

    private Date createdAt;

}
