package com.hairsalonbookingapp.hairsalon.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DiscountProgramRequest {

    @NotBlank(message = "Program name must not be blank!")
    private String name;

    private String description;

    @NotBlank(message = "Start date must not be blank!")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Invalid date!")
    private String startDate;

    @NotBlank(message = "End date must not be blank!")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Invalid date!")
    private String endDate;

    /*@NotBlank(message = "Status must not be blank!")
    @Pattern(regexp = "NotStart|InProcess|End", message = "Invalid status!")
    private String status = "Not start";*/

    @Min(value = 0, message = "Percentage must be at least 0")
    @Max(value = 100, message = "Percentage maximum is 100")
    private double percentage;

    private long pointRequest;
}
