package com.hairsalonbookingapp.hairsalon.model;

import com.hairsalonbookingapp.hairsalon.entity.AccountForCustomer;
import com.hairsalonbookingapp.hairsalon.entity.AccountForEmployee;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class RequestSoftwareSupportApplication {
//    @Id
//    @Column(unique = true)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int softwareSupportApplicationId;

    private String description;

    private String img;
}
