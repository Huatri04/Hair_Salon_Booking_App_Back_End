package com.hairsalonbookingapp.hairsalon.repository;

import com.hairsalonbookingapp.hairsalon.entity.DiscountCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountCodeRepository extends JpaRepository<DiscountCode, Long> {
    DiscountCode findDiscountCodeById(long id);
    DiscountCode findDiscountCodeByDiscountCode(String discountCode);
}
