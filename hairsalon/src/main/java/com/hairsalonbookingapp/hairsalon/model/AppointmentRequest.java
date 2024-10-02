package com.hairsalonbookingapp.hairsalon.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AppointmentRequest {
    @NotBlank(message = "StylistId must not be blank!")
    String stylistId;

    @NotBlank(message = "SlotId must not be blank!")
    long slotId;

    @NotBlank(message = "ServiceId must not be blank!")
    long serviceId;

    @NotBlank(message = "DiscountCodeId must not be blank!")
    String discountCodeId;
}
