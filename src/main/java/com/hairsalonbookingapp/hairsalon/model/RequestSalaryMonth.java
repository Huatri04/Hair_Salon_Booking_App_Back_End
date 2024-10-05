package com.hairsalonbookingapp.hairsalon.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.Month;

@Data
public class RequestSalaryMonth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int salaryMonthId;

//    private double commessionOveratedFromKPI;
//
//    private double fineUnderatedFromKPI;

    private Month month;

//    private double sumSalary;
}
