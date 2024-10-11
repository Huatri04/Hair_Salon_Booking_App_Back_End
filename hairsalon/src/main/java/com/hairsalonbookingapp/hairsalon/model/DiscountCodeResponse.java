package com.hairsalonbookingapp.hairsalon.model;

import com.hairsalonbookingapp.hairsalon.entity.AccountForCustomer;
import com.hairsalonbookingapp.hairsalon.entity.Appointment;
import com.hairsalonbookingapp.hairsalon.entity.DiscountProgram;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class DiscountCodeResponse {
    private long id;
    private String discountCode;
    private double percentage;
    private String programName;
    private String customerName;
}
