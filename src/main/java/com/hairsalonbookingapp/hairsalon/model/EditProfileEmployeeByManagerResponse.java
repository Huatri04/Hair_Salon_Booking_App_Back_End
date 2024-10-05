package com.hairsalonbookingapp.hairsalon.model;

import lombok.Data;

@Data
public class EditProfileEmployeeByManagerResponse {
    private String employeeId;

    private String name;

    private String img;

    private String stylistLevel;

    private Long stylistSelectionFee;

    private Integer KPI;
}
