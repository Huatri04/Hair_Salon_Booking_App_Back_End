package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "ShiftInWeek")
public class ShiftInWeek {
    @Id
    @Column(unique = true, nullable = false)
    private Date dayOfWeek;

    private Date startHour;

    private String shiftEmployee;

    private Date endHour;

    private Date dateOfRequest;
}
