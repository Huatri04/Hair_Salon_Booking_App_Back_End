package com.hairsalonbookingapp.hairsalon.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hairsalonbookingapp.hairsalon.entity.SalaryCaculationFormula;
import com.hairsalonbookingapp.hairsalon.entity.SalaryMonth;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;

import java.util.List;

@Data
public class RequestUpdateProfileEmployeeByManager {
//    @Id
//    @Column(unique = true, nullable = false)
//    private String employeeId;

//    @OneToMany(mappedBy = "employee")
//    @JsonIgnore
//    private List<SalaryMonth> salaryMonths;

    private int salaryCaculationFormulaId;

    private long stylistSelectionFee;

    private String stylistLevel;

    private int KPI;
}
