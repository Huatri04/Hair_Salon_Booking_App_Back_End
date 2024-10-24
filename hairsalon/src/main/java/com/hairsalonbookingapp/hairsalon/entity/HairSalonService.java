package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class HairSalonService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //@NotBlank(message = "Service name must not be blank!")
    private String name;

    //@NotBlank(message = "Service cost must not be blank!")
    //@Pattern(regexp = "^\\d+$", message = "Invalid cost!")
    private double cost;

    //@NotBlank(message = "Time must not be blank!")
    //@Pattern(regexp = "^\\d+$", message = "Invalid time!")
    private int timeOfService;

    private String image;

    private String description; //MÔ TẢ DỊCH VỤ

    private boolean isAvailable = true;   //SERVICE CÓ KHẢ DỤNG KHÔNG

    @ManyToMany(mappedBy = "hairSalonServices")
    List<Appointment> appointments;
}
