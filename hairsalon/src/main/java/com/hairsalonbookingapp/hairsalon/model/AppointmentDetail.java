package com.hairsalonbookingapp.hairsalon.model;

import com.hairsalonbookingapp.hairsalon.entity.AccountForEmployee;
import com.hairsalonbookingapp.hairsalon.entity.HairSalonService;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AppointmentDetail {
    private long id;
    private double totalCost;
    private String date;
    private String startHour;
    private String customer;  // USERNAME
    private EmployeeInfo stylist;
    private List<HairSalonServiceResponse> service;
    private boolean isCompleted;
    private double stylistFee;
    private String discountCode;
    private String status;
    private boolean isSystemChose;
}
