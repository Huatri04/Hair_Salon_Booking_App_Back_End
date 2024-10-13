package com.hairsalonbookingapp.hairsalon.repository;

import com.hairsalonbookingapp.hairsalon.entity.DiscountCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountCodeRepository extends JpaRepository<DiscountCode, String> {
    DiscountCode findDiscountCodeById(String id);
    //DiscountCode findDiscountCodeByDiscountCode(String discountCode);
}