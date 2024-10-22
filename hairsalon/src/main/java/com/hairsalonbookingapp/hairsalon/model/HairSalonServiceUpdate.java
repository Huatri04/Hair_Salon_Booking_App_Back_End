package com.hairsalonbookingapp.hairsalon.model;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class HairSalonServiceUpdate {
    //@Pattern(regexp = "^[a-zA-Z0-9 ]*$\n|", message = "Invalid name!")
    private String name;

    @Min(value = 0, message = "Invalid cost!")
    private double cost;

    @Min(value = 0, message = "Invalid time!")
    private int timeOfService;

    private String image;

    private String description;
}
