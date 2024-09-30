package com.hairsalonbookingapp.hairsalon.model;

import com.hairsalonbookingapp.hairsalon.entity.AccountForEmployee;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.Month;

@Data
public class SalaryMonthResponse {
    private int salaryMonthId;

    private long basicSalary;

    private long CommessionOveratedFromKPI;

    private long fineUnderatedFromKPI;

    private Month month;

    private long sumSalary;

    @ManyToOne
    @JoinColumn(name = "employeeId", nullable = false) // day la foreign key
    private AccountForEmployee employee;

    private boolean isDeleted = false;
}
