package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class DiscountCode {

    @Id
    @Column(unique = true)
    private String id;

    private String status;

    @ManyToOne
    @JoinColumn(name = "discountProgram_id")
    DiscountProgram discountProgram;
}
