package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class DiscountProgram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //@NotBlank(message = "Program name must not be blank!")
    @Column(unique = true)
    private String name;  // TÊN PROGRAM

    private String description;  // DESCRIPTION

    //@NotBlank(message = "Start date must not be blank!")
    //@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$\n", message = "Invalid date!")
    private String startDate;  // NGÀY BẮT ĐẦU

    //@NotBlank(message = "End date must not be blank!")
    //@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$\n", message = "Invalid date!")
    private String endDate;  // NGÀY KẾT THÚC

    /*@NotBlank(message = "Status must not be blank!")
    @Pattern(regexp = "NotStart|InProcess|End", message = "Invalid status!")*/
    private String status = "Not Start";   // STATUS MẶC ĐỊNH KHI MỚI TẠO

    //@NotBlank(message = "Percentage must not be blank!")
    //@Min(value = 0, message = "Percentage must be at least 0")
    //@Max(value = 100, message = "Percentage must be at most 100")
    private double percentage;      // GIẢM BAO NHIÊU %

    private long pointRequest;      // ĐIỂM YÊU CẦU ĐỂ ĐỔI MÃ

    @OneToMany(mappedBy = "discountProgram")
    List<DiscountCode> discountCodes;

}
