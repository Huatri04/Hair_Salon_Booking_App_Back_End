package com.hairsalonbookingapp.hairsalon.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hairsalonbookingapp.hairsalon.entity.SalaryMonth;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;

import java.util.List;

@Data
public class RequestUpdateProfileEmployeeByManager {
    @Id
    @Column(unique = true, nullable = false)
    private String employeeId;

    @NotBlank(message = "Name can not blank!", groups = CreatedBy.class) //ko cho de trong, neu de trong se hien messsage "Name can not blank!"
    private String name;

    @OneToMany(mappedBy = "employee")
    @JsonIgnore
    private List<SalaryMonth> salaryMonths;

    private String stylistLevel;

    private long stylistSelectionFee;

    private int KPI;
}
