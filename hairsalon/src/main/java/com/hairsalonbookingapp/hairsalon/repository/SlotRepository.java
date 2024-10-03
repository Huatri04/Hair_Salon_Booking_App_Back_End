package com.hairsalonbookingapp.hairsalon.repository;

import com.hairsalonbookingapp.hairsalon.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    List<Slot> findSlotsByShiftEmployee_Id(long shiftEmployeeId);
    Slot findSlotById(long id);
    Slot findSlotByIdAndStatusTrue(long id);
    List<Slot> findSlotsByShiftEmployee_IdAndStatusTrue(long shiftEmployeeId);
}
