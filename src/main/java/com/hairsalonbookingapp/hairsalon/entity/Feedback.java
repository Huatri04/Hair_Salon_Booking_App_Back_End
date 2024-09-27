package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.*;
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

    @ManyToOne
    @JoinColumn(name = "phoneNumber") // day la foreign key
    private AccountForCustomer customer;

    private boolean isDeleted = false;
}
