package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "Appointment")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long appointmentId;

    private double cost;

    private String status = "Appointment sent!";

    private boolean isCompleted = false;

    private boolean isDeleted = false;

    private Date date;

    @OneToOne
    @JoinColumn(name = "slotId")
    private Slot slot;

    @ManyToOne
    @JoinColumn(name = "CustomerId")
    private AccountForCustomer accountForCustomer;

    @ManyToOne
    @JoinColumn(name = "ServiceId")
    private HairSalonService hairSalonService;

    @OneToOne
    @JoinColumn(name = "discountCodeId")     // MÃ GIẢM GIÁ
    private DiscountCode discountCode;


}
