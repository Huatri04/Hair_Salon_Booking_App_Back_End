package com.hairsalonbookingapp.hairsalon.model;

import lombok.Data;

import java.util.List;

@Data
public class AppointmentResponsePage {
    List<AppointmentResponseInfo> content;
    int pageNumber;
    long totalElements;
    int totalPages;
}
