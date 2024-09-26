package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;


@Getter
@Setter
@Entity
@Table(name = "AccountForCustomer")
public class AccountForCustomer implements UserDetails {
    @Email(message = "Email invalid!")
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

    private String feedbackId;

    private String discountCodeId;

    private String appointmentId;

    private boolean Status;

    boolean isDeleted = false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.phoneNumber;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
