package com.hairsalonbookingapp.hairsalon.model;

import com.hairsalonbookingapp.hairsalon.entity.AccountForEmployee;
import com.hairsalonbookingapp.hairsalon.entity.ShiftInWeek;
import com.hairsalonbookingapp.hairsalon.entity.Slot;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
public class ShiftEmployeeResponse {
    private long id;
    private String name;
    private boolean status;
    private String dayInWeek;
    private String employeeId;
}
