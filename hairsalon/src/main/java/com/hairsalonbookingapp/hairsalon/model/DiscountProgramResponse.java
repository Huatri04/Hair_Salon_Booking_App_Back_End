package com.hairsalonbookingapp.hairsalon.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class DiscountProgramResponse {
    private long id;
    private String name;
    private String description;
    private String startDate;
    private String endDate;
    private String status;
    private double percentage;
    private long pointRequest;
}
