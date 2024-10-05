package com.hairsalonbookingapp.hairsalon.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "SalaryCaculationFormula")
public class SalaryCaculationFormula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int salaryCaculationFormulaId;

    @Min(value = 0, message = "basic salary must be at least 0")
    private Double basicSalary;

    @Min(value = 0, message = "commession Overated Based Service must be at least 0")
    private Double commessionOveratedBasedService;

    @Min(value = 0, message = "fine Underated Based Service must be at least 0")
    private Double fineUnderatedBasedService;

    private boolean isDeleted = false;

    @OneToMany(mappedBy = "salaryCaculationFormula")
    @JsonIgnore
    private List<SalaryMonth> salaryMonths;

    @OneToMany(mappedBy = "employeeId")
    @JsonIgnore
    private List<AccountForEmployee> employees;
}
