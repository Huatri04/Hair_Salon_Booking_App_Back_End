package com.hairsalonbookingapp.hairsalon.model;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AppointmentUpdate {


    String stylistId;

    @Size(min = 0, message = "Invalid slotId!")
    long slotId;

    @Size(min = 0, message = "Invalid serviceId!")
    long serviceId;

    String discountCodeId;
}
