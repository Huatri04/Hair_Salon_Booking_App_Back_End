package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Cost must not be blank!")
    @Pattern(regexp = "^\\d+$", message = "Invalid cost!")
    private double cost;

    private boolean status;

    @OneToOne
    @JoinColumn(name = "slotId")
    Slot slot;

    @ManyToOne
    @JoinColumn(name = "CustomerId")
    AccountForCustomer accountForCustomer;

    @ManyToOne
    @JoinColumn(name = "ServiceId")
    HairSalonService hairSalonService;

    @OneToOne
    @JoinColumn(name = "discountCodeId")
    DiscountCode discountCode;
}
