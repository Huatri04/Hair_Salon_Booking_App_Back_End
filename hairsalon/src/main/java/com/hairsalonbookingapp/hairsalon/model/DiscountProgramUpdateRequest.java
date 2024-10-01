package com.hairsalonbookingapp.hairsalon.model;

import jakarta.validation.constraints.*;
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
    private String endDate;

    /*@Pattern(regexp = "NotStart|InProcess|End", message = "Invalid status!")
    @NotBlank(message = "Status must not be blank!")
    private String status;*/

    @Min(value = 0, message = "Percentage must be at least 0")
    @Max(value = 100, message = "Percentage must be at most 100")
    @NotBlank(message = "Percentage must not be blank!")
    private double percentage;

    @Size(min = 0, message = "Invalid amount!")
    @NotBlank(message = "Amount must not be blank!")
    private int amount;
}