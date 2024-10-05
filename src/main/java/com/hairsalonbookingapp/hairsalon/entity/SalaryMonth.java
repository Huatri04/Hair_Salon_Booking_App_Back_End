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

    private double commessionOveratedFromKPI;

    private double fineUnderatedFromKPI;

    @Enumerated(EnumType.STRING)
    private Month month;

    private double sumSalary;

    @ManyToOne
    @JoinColumn(name = "salaryCaculationFormulaId", nullable = true) // day la foreign key
    private SalaryCaculationFormula salaryCaculationFormula;

    @ManyToOne
    @JoinColumn(name = "employeeId", nullable = false) // day la foreign key
    private AccountForEmployee employee;

    private boolean isDeleted = false;
}
