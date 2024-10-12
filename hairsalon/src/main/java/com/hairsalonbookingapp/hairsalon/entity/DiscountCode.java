package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class DiscountCode {
    @Id
    private String id; // DISCOUNT CODE

    @ManyToOne
    @JoinColumn(name = "discountProgramId")
    DiscountProgram discountProgram;

    @ManyToOne
    @JoinColumn(name = "customerId", nullable = true)
    AccountForCustomer accountForCustomer;

    @OneToOne(mappedBy = "discountCode")
    Appointment appointment;
}
