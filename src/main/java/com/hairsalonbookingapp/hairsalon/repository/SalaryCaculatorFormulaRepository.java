package com.hairsalonbookingapp.hairsalon.repository;

import com.hairsalonbookingapp.hairsalon.entity.Feedback;
import com.hairsalonbookingapp.hairsalon.entity.SalaryCaculationFormula;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalaryCaculatorFormulaRepository extends JpaRepository<SalaryCaculationFormula, Integer> {
    Optional<SalaryCaculationFormula> findTopByOrderBySalaryCaculationFormulaIdDesc();
    SalaryCaculationFormula findSalaryCaculationFormulaBySalaryCaculationFormulaId(int salaryCaculationFormulaId);
    List<SalaryCaculationFormula> findSalaryCaculationFormulasByIsDeletedFalse();
}
