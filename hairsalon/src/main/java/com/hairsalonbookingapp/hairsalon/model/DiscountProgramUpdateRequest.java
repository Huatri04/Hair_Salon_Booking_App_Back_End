package com.hairsalonbookingapp.hairsalon.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.sql.Date;

@Data
public class DiscountProgramUpdateRequest {
    @Pattern(regexp = "^[a-zA-Z0-9 ]*$\n|", message = "Invalid name!")
    private String name;

    @Pattern(regexp = "^[a-zA-Z0-9 ]*$\n|", message = "Invalid description!")
    private String description;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$\n|", message = "Invalid date!")
    private String startDate;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$\n|", message = "Invalid date!")
    private Date endDate;

    @Pattern(regexp = "NotStart|InProcess|End", message = "Invalid status!")
    private String status;

    @Pattern(regexp = "^[0-9]+$\n|", message = "Invalid percentage!")
    private double percentage;

    @Pattern(regexp = "^[0-9]+$\n|", message = "Invalid amount!")
    private int amount;
}
