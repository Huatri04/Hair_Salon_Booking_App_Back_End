package com.hairsalonbookingapp.hairsalon.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class HairSalonServiceResponse {
    private long id;
    private String name;
    private double cost;
    private int timeOfService;
    private String image;
    private String description;
    private boolean isAvailable;
}
