package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class ShiftEmployee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String status;

    @ManyToOne
    @JoinColumn(name = "dayInWeek_Id")
    ShiftInWeek shiftInWeek;

    @ManyToOne
    @JoinColumn(name = "employee_Id")
    AccountForEmployee accountForEmployee;

    @OneToMany(mappedBy = "shiftEmployee")
    List<Slot> slots;
}
