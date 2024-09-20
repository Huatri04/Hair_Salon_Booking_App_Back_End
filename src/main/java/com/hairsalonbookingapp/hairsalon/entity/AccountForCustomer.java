package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class AccountForCustomer {
    @Pattern(regexp = "^[\\w.-]+@[\\w-]+\\.[\\w]{2,}$", message = "email is invalid!")
    @NotBlank(message = "email must not blank!")
    private String email;

    @NotBlank(message = "Name must not blank!")
    @Size(min = 3, message = "Name must be more than 3 characters")
    private String name;

    @Min(value = 0, message = "Score must at least 0")
    private long score;

    @Id
    @Column(unique = true)
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "phone number is invalid!")
    @NotBlank(message = "phone number must not blank!")
    private String phoneNumber;

    private Date creatAt;

    @NotBlank(message = "Password can not blank!")
    @Size(min = 6, message = "Password must be more than 6 characters")
    private String password;

    private boolean Status;

    boolean isDeleted = false;

}
