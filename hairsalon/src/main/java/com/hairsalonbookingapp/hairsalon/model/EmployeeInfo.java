package com.hairsalonbookingapp.hairsalon.model;

import lombok.Data;

import java.util.Date;

@Data
public class EmployeeInfo {
    String id;
    String username;
    String name;
    String img;
    String email;
    String phoneNumber;
    String degrees; // Bằng cấp // [Stylist]
    double basicSalary;
    Date createdAt;
    String role;
    String stylistLevel; // [Stylist]
    double expertStylistBonus; // phí trả thêm cho expert stylist // [Stylist]
    int KPI; // KPI của stylist // [Stylist]
    int completedShift;  // SỐ SHIFT STYLIST HOÀN THÀNH -> CỘNG DỒN LIÊN TỤC TỚI KHI ĐỦ BAO NHIÊU ĐÓ THÌ DỪNG LẠI ĐỂ
    // TÍNH CÁC THỨ KHÁC RỒI RESET VỀ 0

    int completedSlot;
}
