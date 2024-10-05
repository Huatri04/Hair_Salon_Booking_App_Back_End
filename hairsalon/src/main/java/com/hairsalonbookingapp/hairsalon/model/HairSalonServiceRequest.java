package com.hairsalonbookingapp.hairsalon.model;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class HairSalonServiceRequest {
    @NotBlank(message = "Service name must not be blank!")
    private String name;

    @Min(value = 0, message = "Invalid cost!")
    private double cost;

    @Min(value = 0, message = "Invalid time!")
    @Max(value = 60, message = "Invalid time!")
    private int timeOfService;

    private String image;
}
