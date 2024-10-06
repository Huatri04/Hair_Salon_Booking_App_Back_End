package com.hairsalonbookingapp.hairsalon.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ShiftWeekResponse {
    private String dayOfWeek;
    private String startHour;
    private String endHour;
    private boolean isAvailable;
}
