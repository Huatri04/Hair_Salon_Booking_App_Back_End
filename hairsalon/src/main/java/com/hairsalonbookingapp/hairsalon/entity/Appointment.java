package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Cost must not be blank!")
    @Pattern(regexp = "^\\d+$", message = "Invalid cost!")
    private double cost;

    private String status;

    @OneToOne
    @JoinColumn(name = "slot_Id")
    Slot slot;

    @ManyToOne
    @JoinColumn(name = "Customer_Id")
    AccountForCustomer accountForCustomer;

    @ManyToOne
    @JoinColumn(name = "Service_Id")
    HairSalonService hairSalonService;

    @OneToOne
    @JoinColumn(name = "discountCode_Id")
    DiscountCode discountCode;
}
