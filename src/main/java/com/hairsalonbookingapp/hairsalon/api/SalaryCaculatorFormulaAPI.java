package com.hairsalonbookingapp.hairsalon.api;

import com.hairsalonbookingapp.hairsalon.entity.SalaryCaculationFormula;
import com.hairsalonbookingapp.hairsalon.entity.SalaryMonth;
import com.hairsalonbookingapp.hairsalon.model.*;
import com.hairsalonbookingapp.hairsalon.service.SalaryCaculationFormulaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/SalaryCaculationFormula")
@CrossOrigin("*")
@SecurityRequirement(name = "api") // tao controller moi nho copy qua
public class SalaryCaculatorFormulaAPI {

    @Autowired
    SalaryCaculationFormulaService salaryCaculationFormulaService;

    @PostMapping
//    @PreAuthorize("hasAuthority('customer')")
    public ResponseEntity createSalaryCaculationFormula(@Valid @RequestBody RequestSalaryCaculatorFormula requestSalaryCaculatorFormula){
        SalaryCaculationFormulaResponse salaryCaculationFormulaResponse = salaryCaculationFormulaService.createSalaryCaculationFormula(requestSalaryCaculatorFormula);
        return ResponseEntity.ok(salaryCaculationFormulaResponse);
    }

    @PutMapping("{id}")
    public ResponseEntity updatedCaculationFormula(@Valid @RequestBody RequestUpdateSalaryCaculationFormula requestUpdateSalaryCaculationFormula, @PathVariable int id){ //@PathVariable de tim thang id tu FE
        UpdateSalaryCaculatorFormulaResponse oldSalaryCaculatorFormula = salaryCaculationFormulaService.updatedSalaryCaculationFormula(requestUpdateSalaryCaculationFormula, id);
        return ResponseEntity.ok(oldSalaryCaculatorFormula);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteSalaryCaculatorFormula(@PathVariable int id){
        SalaryCaculationFormulaResponse salaryCaculationFormulaResponse = salaryCaculationFormulaService.deleteSalaryCaculationFormula(id);
        return ResponseEntity.ok(salaryCaculationFormulaResponse);
    }

    @GetMapping
    public ResponseEntity getAllSalaryCaculatorFormula(){
        List<SalaryCaculationFormula> salaryCaculationFormulas = salaryCaculationFormulaService.getAllSalaryCaculationFormula();
        return ResponseEntity.ok(salaryCaculationFormulas);
    }

    @GetMapping("{id}")
    public ResponseEntity getSalaryCaculatorFormulaInfo(@PathVariable int id){
        SalaryCaculationFormulaInfoResponse salaryCaculationFormulaResponse = salaryCaculationFormulaService.getInfoSalaryCaculationFormula(id);
        return ResponseEntity.ok(salaryCaculationFormulaResponse);
    }
}
