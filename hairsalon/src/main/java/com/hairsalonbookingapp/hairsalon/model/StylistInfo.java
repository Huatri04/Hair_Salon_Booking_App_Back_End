package com.hairsalonbookingapp.hairsalon.model;

import lombok.Data;

@Data
public class StylistInfo {
    String id;
    String name;
    String img;
    String phoneNumber;
    String stylistLevel;
    String status;
    Double expertStylistBonus;
}
