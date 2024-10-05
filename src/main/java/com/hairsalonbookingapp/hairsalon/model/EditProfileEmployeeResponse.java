package com.hairsalonbookingapp.hairsalon.model;

import com.hairsalonbookingapp.hairsalon.entity.SalaryCaculationFormula;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;

@Data
public class EditProfileEmployeeResponse {

    private String id;

    private String name;

    private String img;

    private String email;

    private String phoneNumber;

    private SalaryCaculationFormula salaryCaculationFormula;

    private String stylistLevel;
}
