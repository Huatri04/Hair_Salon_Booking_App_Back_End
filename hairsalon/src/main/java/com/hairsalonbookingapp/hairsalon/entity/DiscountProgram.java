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
    private String name;

    private String description;

    //@NotBlank(message = "Start date must not be blank!")
    //@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$\n", message = "Invalid date!")
    private String startDate;

    //@NotBlank(message = "End date must not be blank!")
    //@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$\n", message = "Invalid date!")
    private String endDate;

    /*@NotBlank(message = "Status must not be blank!")
    @Pattern(regexp = "NotStart|InProcess|End", message = "Invalid status!")*/
    private String status = "NotStart";

    //@NotBlank(message = "Percentage must not be blank!")
    //@Min(value = 0, message = "Percentage must be at least 0")
    //@Max(value = 100, message = "Percentage must be at most 100")
    private double percentage;      // GIẢM BAO NHIÊU %

    //@NotBlank(message = "Amount must not be blank!")
    //@Size(min = 0, message = "Invalid amount!")
    private int amount;      // SỐ LƯỢNG MÃ

    @OneToMany(mappedBy = "discountProgram")
    List<DiscountCode> discountCodes;

}
