package com.hairsalonbookingapp.hairsalon.model;

import lombok.Data;

import java.util.List;

@Data
public class CustomerResponsePage {
    List<CustomerAccountInfo> content;
    int pageNumber;
    long totalElements;
    int totalPages;
}
