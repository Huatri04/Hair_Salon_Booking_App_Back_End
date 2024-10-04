package com.hairsalonbookingapp.hairsalon.model;

import com.hairsalonbookingapp.hairsalon.entity.AccountForCustomer;
import com.hairsalonbookingapp.hairsalon.entity.Appointment;
import com.hairsalonbookingapp.hairsalon.entity.DiscountProgram;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class DiscountCodeResponse {
    private String id;
    private long discountProgramId;
    private String customerId; //PHONE NUMBER
}
