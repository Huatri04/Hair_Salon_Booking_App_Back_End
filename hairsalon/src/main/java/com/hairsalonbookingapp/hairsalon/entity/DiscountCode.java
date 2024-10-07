package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class DiscountCode {
    /*@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;*/

    @Id
    private String id;

    //private String discountCode;

    //private boolean isAvailable = true;

    @ManyToOne
    @JoinColumn(name = "discountProgramId")
    DiscountProgram discountProgram;

    @ManyToOne
    @JoinColumn(name = "customerId", nullable = true)
    AccountForCustomer accountForCustomer;

    @OneToOne(mappedBy = "discountCode")
    Appointment appointment;
}
