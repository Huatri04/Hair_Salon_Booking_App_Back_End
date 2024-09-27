package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
public class HairSalonService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Name must not be blank!")
    @Column(unique = true)
    private String name;

    @NotBlank(message = "Cost must not be blank!")
    private double cost;

    @NotBlank(message = "Time must not be blank!")
    private long timeOfService;

    private boolean isDeleted = false;
}
