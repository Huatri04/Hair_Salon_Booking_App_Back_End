package com.hairsalonbookingapp.hairsalon.model.request;

import lombok.Data;

@Data
public class CompleteAppointmentRequest {
    String stylistId;
    String startSlot;
    String date;
}
