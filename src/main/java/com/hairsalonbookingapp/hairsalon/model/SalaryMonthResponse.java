package com.hairsalonbookingapp.hairsalon.model;

import com.hairsalonbookingapp.hairsalon.entity.AccountForEmployee;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.Month;

@Data
public class SalaryMonthResponse {
    private int salaryMonthId;

    private double commessionOveratedFromKPI;

    private double fineUnderatedFromKPI;

    @Enumerated(EnumType.STRING)
    private Month month;

    private double sumSalary;

    @ManyToOne
    @JoinColumn(name = "employeeId", nullable = false) // day la foreign key
    private AccountForEmployee employee;

    private boolean isDeleted = false;
}