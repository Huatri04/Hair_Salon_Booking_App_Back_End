package com.hairsalonbookingapp.hairsalon.model;

import com.hairsalonbookingapp.hairsalon.entity.AccountForCustomer;
import com.hairsalonbookingapp.hairsalon.entity.DiscountCode;
import com.hairsalonbookingapp.hairsalon.entity.HairSalonService;
import com.hairsalonbookingapp.hairsalon.entity.Slot;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class AppointmentResponse {
    private long id;
    private double cost;
    private String day;
    private String startHour;
    private String customer;  // USERNAME
    private List<String> service;
    private String stylist;


    /*private long slotId;
    private String CustomerId;
    private long ServiceId;
    private String discountCodeId;*/
}
