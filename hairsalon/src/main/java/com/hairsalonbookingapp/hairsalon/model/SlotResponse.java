package com.hairsalonbookingapp.hairsalon.model;

import com.hairsalonbookingapp.hairsalon.entity.Appointment;
import com.hairsalonbookingapp.hairsalon.entity.ShiftEmployee;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class SlotResponse {
    private long id;
    private String startSlot;
    private boolean status;
    private long shiftEmployeeId;
    private boolean isCompleted;
}
