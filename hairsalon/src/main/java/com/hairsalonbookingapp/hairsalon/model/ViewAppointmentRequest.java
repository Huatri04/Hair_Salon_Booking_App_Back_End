package com.hairsalonbookingapp.hairsalon.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ViewAppointmentRequest {
    String stylistId;
    String date;
}
