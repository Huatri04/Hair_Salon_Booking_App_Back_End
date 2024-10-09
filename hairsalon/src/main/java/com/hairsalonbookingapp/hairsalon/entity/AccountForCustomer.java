package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AccountForCustomer implements UserDetails {
    @Pattern(regexp = "^[\\w.-]+@[\\w-]+\\.[\\w]{2,}$", message = "Email is invalid!")
    @NotBlank(message = "Email must not be blank!")
    private String email;

    @NotBlank(message = "Username must not be blank!")
    @Size(min = 3, message = "Username must be more than 3 characters!")
    private String customerName;

    @Min(value = 0, message = "Score must be at least 0!")
    private long point;

    @Id
    @Column(unique = true)
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Phone number is invalid!")
    @NotBlank(message = "Phone number must not be blank!")
    private String phoneNumber;

    private Date creatAt;

    @NotBlank(message = "Password must not be blank!")
    @Size(min = 6, message = "Password must be more than 6 characters")
    private String password;

    boolean isDeleted = false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
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

    @OneToMany(mappedBy = "accountForCustomer")
    List<Appointment> appointments;

    @OneToMany(mappedBy = "accountForCustomer")
    List<DiscountCode> discountCodes;
}
