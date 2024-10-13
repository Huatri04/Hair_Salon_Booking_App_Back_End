package com.hairsalonbookingapp.hairsalon.model;

import lombok.Data;

@Data
public class KPITotal {
    String stylistId;
    int KPI;
    int total; // TỔNG LỊCH HẸN STYLIST NHẬN TRONG THÁNG
}
