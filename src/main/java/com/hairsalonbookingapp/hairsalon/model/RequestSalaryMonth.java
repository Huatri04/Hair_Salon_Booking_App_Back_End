package com.hairsalonbookingapp.hairsalon.model;

import lombok.Data;

import java.time.Month;

@Data
public class RequestSalaryMonth {
    private long basicSalary;

    private long CommessionOveratedFromKPI;

    private long fineUnderatedFromKPI;

    private Month month;

    private long sumSalary;
}
