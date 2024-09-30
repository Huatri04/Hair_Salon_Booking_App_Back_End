package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String startSlot;

    @NotBlank(message = "Date must not be blank!")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$\n", message = "Invalid date!")
    private String date;

    @ManyToOne
    @JoinColumn(name = "shiftEmployee_Id")
    ShiftEmployee shiftEmployee;

    @OneToMany(mappedBy = "slot")
    List<Appointment> appointments;
}
