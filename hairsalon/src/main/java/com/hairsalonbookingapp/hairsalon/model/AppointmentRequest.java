package com.hairsalonbookingapp.hairsalon.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AppointmentRequest {
    String startHour;
    String date;
    String stylistId;
    List<Long> serviceIdList;
    String discountCode;
}
