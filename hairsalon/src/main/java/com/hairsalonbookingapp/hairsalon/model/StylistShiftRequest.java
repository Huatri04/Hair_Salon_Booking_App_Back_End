package com.hairsalonbookingapp.hairsalon.model;

import lombok.Data;

@Data
public class StylistShiftRequest {
    String day1;
    String day2;
    String day3; //  HIỆN TẠI ĐỂ THUẬN TIỆN CHO VIỆC TEST NÊN CHỈ TẠO 3 NGÀY, MỐT CHỈNH LẠI
    String StylistID;
}
