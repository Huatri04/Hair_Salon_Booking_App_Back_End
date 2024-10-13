package com.hairsalonbookingapp.hairsalon.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AppointmentRequest {
    long slotId;
    List<Long> serviceIdList;
    String discountCode;
}
