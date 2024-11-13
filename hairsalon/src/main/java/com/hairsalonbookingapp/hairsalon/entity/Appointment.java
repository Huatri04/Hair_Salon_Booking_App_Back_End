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
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;            // ID LỊCH HẸN

    private double cost;        // GIÁ TIỀN

    //private String status = "Appointment sent!";  // HIỂN THỊ TRẠNG THÁI ĐƠN HIỆN TẠI
    // KHÁCH ĐẶT ĐƠN VÀ STAFF NHẬN ĐƠN THÔNG QUA GET TRÊN DB

    private boolean isDeleted = false;  // CHỈ CUSTOMER ĐƯỢC PHÉP HỦY ĐƠN

    private boolean isCompleted = false;  // ĐƠN CHƯA HOÀN THÀNH

    private String status = "Chưa phục vụ";// Đang phục vụ, Đã thanh toán

    private String date;
    private String startHour;
    private String stylist;

    @OneToOne
    @JoinColumn(name = "slotId")            // THỜI GIAN + STYLIST
    Slot slot;

    @ManyToOne
    @JoinColumn(name = "CustomerId")        // KHÁCH ĐẶT
    AccountForCustomer accountForCustomer;

    @ManyToMany
    @JoinTable(
            name = "service_appointment", // Tên bảng trung gian
            joinColumns = @JoinColumn(name = "appointment_id"), // Khóa ngoại từ bảng Appointment
            inverseJoinColumns = @JoinColumn(name = "service_id") // Khóa ngoại từ bảng Service
    )
    List<HairSalonService> hairSalonServices;

    @OneToOne
    @JoinColumn(name = "discountCodeId")     // MÃ GIẢM GIÁ
    DiscountCode discountCode;
}
