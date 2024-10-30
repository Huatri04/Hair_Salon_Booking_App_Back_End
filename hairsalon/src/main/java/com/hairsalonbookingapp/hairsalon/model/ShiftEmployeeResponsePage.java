package com.hairsalonbookingapp.hairsalon.model;

import lombok.Data;

import java.util.List;

@Data
public class ShiftEmployeeResponsePage {
    List<ShiftEmployeeResponse> content;
    int pageNumber;
    long totalElements;
    int totalPages;
}
