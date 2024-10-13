package com.hairsalonbookingapp.hairsalon.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeleteAppointmentRequest {
    long id;
    String phonenumber;
}
