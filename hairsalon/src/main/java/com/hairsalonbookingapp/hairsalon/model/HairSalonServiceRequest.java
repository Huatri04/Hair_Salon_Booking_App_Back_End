package com.hairsalonbookingapp.hairsalon.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class HairSalonServiceRequest {
    @NotBlank(message = "Service name must not be blank!")
    private String name;

    @NotBlank(message = "Service cost must not be blank!")
    @Pattern(regexp = "^\\d+$", message = "Invalid cost!")
    private double cost;

    @NotBlank(message = "Time must not be blank!")
    @Pattern(regexp = "^\\d+$", message = "Invalid time!")
    private int timeOfService;

    private String image;
}
