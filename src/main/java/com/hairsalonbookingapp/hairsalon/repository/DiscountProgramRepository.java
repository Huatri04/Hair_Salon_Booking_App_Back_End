package com.hairsalonbookingapp.hairsalon.repository;

import com.hairsalonbookingapp.hairsalon.entity.DiscountProgram;
import com.hairsalonbookingapp.hairsalon.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiscountProgramRepository extends JpaRepository<DiscountProgram, String> {
    Optional<DiscountProgram> findTopByOrderByDiscountProgramIdDesc();
    DiscountProgram findDiscountProgramByDiscountProgramId(String id);
    List<DiscountProgram> findDiscountProgramsByIsDeletedFalse();
}
