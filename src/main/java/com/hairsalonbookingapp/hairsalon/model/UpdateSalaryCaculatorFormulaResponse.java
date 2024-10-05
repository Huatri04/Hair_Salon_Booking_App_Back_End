package com.hairsalonbookingapp.hairsalon.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hairsalonbookingapp.hairsalon.entity.AccountForEmployee;
import com.hairsalonbookingapp.hairsalon.entity.SalaryMonth;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.List;

@Data
public class UpdateSalaryCaculatorFormulaResponse {
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

    @OneToMany(mappedBy = "employee")
    @JsonIgnore
    private List<AccountForEmployee> employees;

    @OneToMany(mappedBy = "salaryCaculationFormula")
    @JsonIgnore
    private List<SalaryMonth> salaryMonths;
}
