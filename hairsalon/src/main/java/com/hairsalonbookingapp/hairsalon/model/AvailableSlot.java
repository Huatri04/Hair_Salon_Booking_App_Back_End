package com.hairsalonbookingapp.hairsalon.model;

import lombok.Data;

@Data
public class AvailableSlot {
    long slotId;
    String stylistName;
    String startHour;
    String stylistLevel;
}
