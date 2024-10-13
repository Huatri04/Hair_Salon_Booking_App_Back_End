package com.hairsalonbookingapp.hairsalon.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ShiftWeekRequest {
    @NotBlank(message = "Day must not be blank!")
    @Pattern(regexp = "MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY")
    private String dayOfWeek;

    @NotBlank(message = "Start hour must not be blank!")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "Invalid time!")
    private String startHour;

    @NotBlank(message = "End hour must not be blank!")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "Invalid time!")
    private String endHour;

}