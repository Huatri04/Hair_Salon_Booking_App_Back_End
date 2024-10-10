package com.hairsalonbookingapp.hairsalon.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TimeService {

    // GIẢ ĐỊNH NGÀY HIỆN TẠI
    public final LocalDate today = LocalDate.of(2024, 10, 10);

    // DANH SÁCH CÁC NGÀY TRONG NĂM
    public List<LocalDate> getAllDaysInYear(int year) {
        List<LocalDate> daysInYear = new ArrayList<>();

        // Ngày bắt đầu là 1/1 của năm nhập vào
        LocalDate startDate = LocalDate.of(year, 1, 1);

        // Lặp qua từng ngày trong năm
        for (int i = 0; i < startDate.lengthOfYear(); i++) {
            daysInYear.add(startDate.plusDays(i));
        }

        return daysInYear;
    }


    // LẤY CÁC NGÀY TỪ NGÀY A ĐẾN NGÀY B
    public List<LocalDate> getDaysBetween(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> daysBetween = new ArrayList<>();

        // Kiểm tra nếu startDate trước endDate
        if (startDate.isAfter(endDate)) {
            System.out.println("Ngày bắt đầu phải trước hoặc bằng ngày kết thúc.");
            return daysBetween; // Trả về danh sách rỗng nếu không hợp lệ
        }

        // Lặp qua các ngày trong khoảng từ startDate đến endDate
        while (!startDate.isAfter(endDate)) {
            daysBetween.add(startDate);
            startDate = startDate.plusDays(1);  // Cộng thêm 1 ngày
        }

        return daysBetween;
    }


    // HÀM LẤY THỜI GIAN HIỆN TẠI (NGÀY, THÁNG, NĂM)
    public LocalDate getToday(){
        return LocalDate.now();
    }


}
