package com.hairsalonbookingapp.hairsalon.repository;

import com.hairsalonbookingapp.hairsalon.entity.ShiftInWeek;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShiftWeekRepository extends JpaRepository<ShiftInWeek, String> {
    ShiftInWeek findShiftInWeekByDayOfWeekAndStatusTrue(String dayOfWeek);
    List<ShiftInWeek> findShiftInWeeksByStatusTrue();
    //List<String> findDayOfWeeksByStatusTrue();
}
