package com.hairsalonbookingapp.hairsalon.repository;

import com.hairsalonbookingapp.hairsalon.entity.DiscountProgram;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiscountProgramRepository extends JpaRepository<DiscountProgram, Long> {
    DiscountProgram findDiscountProgramById(long id);
}
