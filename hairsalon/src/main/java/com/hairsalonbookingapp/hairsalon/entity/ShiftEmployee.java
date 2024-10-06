package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class ShiftEmployee { // DO STYLIST LÀM
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;  // ID CÓ THỂ TỰ GENERATE

    private String name;  // TÊN EMPLOYEE LÀM CA ĐÓ

    private boolean status;  // CHECK XEM CA NÀY CÒN KHẢ DỤNG VỚI STYLIST HAY KO

    private boolean isCompleted = false;

    @ManyToOne
    @JoinColumn(name = "dayInWeek")
    ShiftInWeek shiftInWeek;    // STYLIST CÓ THỂ CÓ NHIỀU CA(THỨ 2,3,4...) TRONG TUẦN

    @ManyToOne
    @JoinColumn(name = "employeeId")
    AccountForEmployee accountForEmployee;   // CHO BIẾT CA ĐÓ AI LÀM

    @OneToMany(mappedBy = "shiftEmployee")
    List<Slot> slots;
}
