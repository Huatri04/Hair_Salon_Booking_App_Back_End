package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;  // ID CÓ THỂ GENERATE

    private String startSlot;  // SLOT BẮT ĐẦU LÚC MẤY GIỜ

    /*@NotBlank(message = "Date must not be blank!")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$\n", message = "Invalid date!")
    private String date;*/

    private boolean isAvailable = true;  // SLOT CÒN TRỐNG KHÔNG

    private boolean isCompleted = false;   // SLOT CHƯA HOÀN THÀNH, XONG 1 KHÁCH THÌ STYLIST CHECK TRUE

    @ManyToOne
    @JoinColumn(name = "shiftEmployeeId")
    ShiftEmployee shiftEmployee;   //   CHO BIẾT SLOT THUỘC CA NÀO, CỦA AI

    @OneToOne(mappedBy = "slot")
    Appointment appointments;
}