package com.hairsalonbookingapp.hairsalon.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "ShiftInWeek")
public class ShiftInWeek {
    @Id
    @Column(unique = true, nullable = false)
    private String dayOfWeek;

    @NotBlank(message = "Start hour must not be blank!")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "Invalid time!")
    private Date startHour;

    @OneToMany(mappedBy = "shiftInWeek")
    @JsonIgnore
    private List<ShiftEmployee> shiftEmployees;

    @NotBlank(message = "End hour must not be blank!")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "Invalid time!")
    private Date endHour;

    private boolean isAvailable = true;
}
