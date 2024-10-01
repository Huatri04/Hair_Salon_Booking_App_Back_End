package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class ShiftInWeek { //CÁC NGÀY TRONG TUẦN (THỨ 2, 3, 4,....)-> DATA ĐC THIẾT LẬP SẴN
    @Id
    @Column(unique = true)
    private String dayOfWeek;

    @NotBlank(message = "Start hour must not be blank!")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "Invalid time!")
    private String startHour;

    @NotBlank(message = "End hour must not be blank!")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "Invalid time!")
    private String endHour;

    /*@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$\n", message = "Invalid date!")
    private String dateApply;*/

    private boolean status; // NGÀY LÀM VIỆC CÓ KHẢ DỤNG KHÔNG

    @OneToMany(mappedBy = "shiftInWeek")
    List<ShiftEmployee> shiftEmployees;
}
