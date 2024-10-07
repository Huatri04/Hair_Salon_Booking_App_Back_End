package com.hairsalonbookingapp.hairsalon.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AppointmentRequest {
    @NotBlank(message = "StylistId must not be blank!")
    String stylistId;

    long slotId;

    long serviceId;

    String discountCodeId;
}
