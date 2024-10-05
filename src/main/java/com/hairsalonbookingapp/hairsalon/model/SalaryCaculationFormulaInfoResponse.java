package com.hairsalonbookingapp.hairsalon.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class SalaryCaculationFormulaInfoResponse {
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
}
