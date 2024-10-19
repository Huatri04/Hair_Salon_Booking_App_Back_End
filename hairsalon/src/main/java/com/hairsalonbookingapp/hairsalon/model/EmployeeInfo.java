package com.hairsalonbookingapp.hairsalon.model;

import lombok.Data;

import java.util.Date;

@Data
public class EmployeeInfo {
    String id;
    String username;
    String name;
    String img;
    String email;
    String phoneNumber;
    String degrees; // Bằng cấp // [Stylist]
    double basicSalary;
    Date createdAt;
    String role;
    String stylistLevel; // [Stylist]
    double expertStylistBonus; // phí trả thêm cho expert stylist // [Stylist]
    int KPI; // KPI của stylist // [Stylist]
    String status;
    boolean isDeleted;
    String days;
    int completedSlot;
}
