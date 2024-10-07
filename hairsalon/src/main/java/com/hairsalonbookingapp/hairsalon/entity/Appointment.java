package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;            // ID LỊCH HẸN

    private double cost;        // GIÁ TIỀN

    private String status = "Appointment sent!";  // HIỂN THỊ TRẠNG THÁI ĐƠN HIỆN TẠI
    // KHÁCH ĐẶT ĐƠN VÀ STAFF NHẬN ĐƠN THÔNG QUA GET TRÊN DB

    private boolean isDeleted = false;  // CHỈ CUSTOMER ĐƯỢC PHÉP HỦY ĐƠN

    @OneToOne
    @JoinColumn(name = "slotId")            // THỜI GIAN + STYLIST
    Slot slot;

    @ManyToOne
    @JoinColumn(name = "CustomerId")        // KHÁCH ĐẶT
    AccountForCustomer accountForCustomer;

    @ManyToOne
    @JoinColumn(name = "ServiceId")         // DỊCH VỤ
    HairSalonService hairSalonService;

    @OneToOne
    @JoinColumn(name = "discountCodeId")     // MÃ GIẢM GIÁ
    DiscountCode discountCode;
}
