package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.*;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long PaymentId;

    private Date createAt;

    private String typePayment;

    @OneToOne
    @JoinColumn(name = "appointmentId")
    private Appointment appointment;

}
