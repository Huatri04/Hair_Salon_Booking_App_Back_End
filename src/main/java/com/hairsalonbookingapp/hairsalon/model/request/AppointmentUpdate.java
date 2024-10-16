package com.hairsalonbookingapp.hairsalon.model.request;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AppointmentUpdate {

    String stylistId;
    @Min(value = 0, message = "Invalid slotID!")
    long slotId;
    @Min(value = 0, message = "Invalid serviceID!")
    long serviceId;
    String discountCodeId;
    List<Long> serviceIdList;
    String discountCode;
}
