package com.hairsalonbookingapp.hairsalon.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hairsalonbookingapp.hairsalon.entity.SalaryMonth;
import com.hairsalonbookingapp.hairsalon.entity.SoftwareSupportApplication;
import com.hairsalonbookingapp.hairsalon.entity.Transaction;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;

import java.util.Date;
import java.util.List;

@Data
public class AccountForEmployeeResponse {
    private String employeeId;
    private String username;
    private String name;
    private String img;
    private String email;
    private String phoneNumber;
    private String role;
    private String token;
    private String stylistLevel;
    private long stylistSelectionFee;
    private int KPI;
    private Date createdAt;
    private String Status;
    private boolean isDeleted;

}
