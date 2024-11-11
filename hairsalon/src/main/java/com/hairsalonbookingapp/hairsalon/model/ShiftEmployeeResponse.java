package com.hairsalonbookingapp.hairsalon.model;

import com.hairsalonbookingapp.hairsalon.entity.AccountForEmployee;
import com.hairsalonbookingapp.hairsalon.entity.ShiftInWeek;
import com.hairsalonbookingapp.hairsalon.entity.Slot;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
public class ShiftEmployeeResponse {
    private long id; // ID SHIFT
    private String dayInWeek;  // THỨ MẤY (THỨ 2,3,4...)
    private String employeeId; // ID STYLIST
    private String name; // AI LÀM
    private boolean isAvailable;  // CÒN KHẢ DỤNG KHÔNG
    private String date; // NGÀY NÀO
    private String startHour;
    private String endHour;
}
