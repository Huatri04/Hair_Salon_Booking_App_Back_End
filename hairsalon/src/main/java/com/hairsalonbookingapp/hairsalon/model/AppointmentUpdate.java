package com.hairsalonbookingapp.hairsalon.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AppointmentUpdate {

    String stylistId;

    @Min(value = 0, message = "Invalid slotID!")
    long slotId;

    @Min(value = 0, message = "Invalid serviceID!")
    long serviceId;

    String discountCodeId;
}
