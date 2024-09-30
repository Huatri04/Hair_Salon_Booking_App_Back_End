package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Month;

@Getter
@Setter
@Entity
@Table(name = "SalaryMonth")
public class SalaryMonth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
