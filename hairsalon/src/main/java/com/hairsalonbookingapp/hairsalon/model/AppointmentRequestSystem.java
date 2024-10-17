package com.hairsalonbookingapp.hairsalon.model;

import lombok.Data;

import java.util.List;

@Data
public class AppointmentRequestSystem {
    String date;
    String startHour;
    List<Long> serviceIdList;
    String discountCode;
}
