package com.hairsalonbookingapp.hairsalon.model;

import lombok.Data;

@Data
public class DeleteAllAppointmentsRequest {
    String stylistId;
    String date;
}
