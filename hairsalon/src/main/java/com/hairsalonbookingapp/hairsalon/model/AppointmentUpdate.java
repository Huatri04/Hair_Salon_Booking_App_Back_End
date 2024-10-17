package com.hairsalonbookingapp.hairsalon.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AppointmentUpdate {
    String startHour;
    String date;
    String stylistId;
    List<Long> serviceIdList;
    String discountCode;
}
