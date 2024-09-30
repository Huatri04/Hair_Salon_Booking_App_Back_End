package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "SoftwareSupportApplication")
public class SoftwareSupportApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int softwareSupportApplicationId;

    @ManyToOne
    @JoinColumn(name = "phoneNumber", nullable = true, referencedColumnName = "phoneNumber") // day la foreign key
    private AccountForCustomer customer;

    @ManyToOne
    @JoinColumn(name = "employeeId", nullable = true, referencedColumnName = "employeeId") // day la foreign key
    private AccountForEmployee employee;

    private String description;

    private String img;

    private boolean isDeleted = false;
}
