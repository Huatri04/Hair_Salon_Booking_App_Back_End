package com.hairsalonbookingapp.hairsalon.model;

import lombok.Data;

@Data
public class DeleteAppointmentRequest {
    long id;
    String phonenumber;
}
