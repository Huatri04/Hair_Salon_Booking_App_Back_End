package com.hairsalonbookingapp.hairsalon.repository;

import com.hairsalonbookingapp.hairsalon.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    List<Slot> findSlotsByShiftEmployeeId(long shiftEmployeeId);
    Slot findSlotById(long id);
    List<Slot> findSlotsByShiftEmployeeIdAndStatusTrue(long shiftEmployeeId);
}
