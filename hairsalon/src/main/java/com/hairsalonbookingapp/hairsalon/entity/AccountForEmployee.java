package com.hairsalonbookingapp.hairsalon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AccountForEmployee implements UserDetails {
    @Id
    @Column(unique = true)
    private String id;

    @NotBlank(message = "Name must not be blank!")
    @Column(unique = true)
    private String username;

    private String img;

    @Pattern(regexp = "^[\\w.-]+@[\\w-]+\\.[\\w]{2,}$", message = "Email is invalid!")
    @NotBlank(message = "Email must not be blank!")
    private String email;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Phone number is invalid!")
    @NotBlank(message = "Phone number must not be blank!")
    private String phoneNumber;

    private String degrees; // Bằng cấp // [Stylist]

    private double basicSalary;

    private Date createdAt;

    @NotBlank(message = "Password must not be blank!")
    @Size(min = 6, message = "Password must be more than 6 characters")
    private String password;

    @NotBlank(message = "Role must not be blank!")
    @Pattern(regexp = "Manager|Stylist|Staff|Admin", message = "Role is invalid!")
    private String role;

    @NotBlank(message = "StylistLevel must not be blank!")
    @Pattern(regexp = "Normal|Expert|NotStylist", message = "StylistLevel is invalid!")
    private String stylistLevel; // [Stylist]

    private double expertStylistBonus; // phí trả thêm cho expert stylist // [Stylist]

    //@NotBlank(message = "Status must not be blank!")
    //@Pattern(regexp = "Workday|On leave", message = "Status is invalid!")
    private String status;

    private int KPI; // KPI của stylist // [Stylist]

    boolean isDeleted = false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return this.username;
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

    @OneToMany(mappedBy = "accountForEmployee")
    List<ShiftEmployee> shiftEmployees;
}
