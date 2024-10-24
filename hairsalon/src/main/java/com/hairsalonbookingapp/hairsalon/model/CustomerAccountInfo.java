package com.hairsalonbookingapp.hairsalon.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
public class CustomerAccountInfo {
    String email;
    String customerName;
    long point;
    String phoneNumber;
    Date creatAt;
    String password;
    boolean isDeleted;
}
