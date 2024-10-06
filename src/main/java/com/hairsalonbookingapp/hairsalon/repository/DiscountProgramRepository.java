package com.hairsalonbookingapp.hairsalon.repository;

import com.hairsalonbookingapp.hairsalon.entity.DiscountProgram;
import com.hairsalonbookingapp.hairsalon.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DiscountProgramRepository extends JpaRepository<DiscountProgram, Integer> {
    Optional<DiscountProgram> findTopByOrderByDiscountProgramIdDesc();
    DiscountProgram findDiscountProgramByDiscountProgramId(int id);
    List<DiscountProgram> findDiscountProgramByName(String name);
    List<DiscountProgram> findDiscountProgramsByIsDeletedFalse();
    Optional<DiscountProgram> findFirstByStartedDateBeforeAndEndedDateAfterAndIsDeletedFalse(LocalDateTime startedDate, LocalDateTime endedDate);
}