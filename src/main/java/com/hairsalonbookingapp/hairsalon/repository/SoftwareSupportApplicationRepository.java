package com.hairsalonbookingapp.hairsalon.repository;

import com.hairsalonbookingapp.hairsalon.entity.Feedback;
import com.hairsalonbookingapp.hairsalon.entity.SoftwareSupportApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SoftwareSupportApplicationRepository extends JpaRepository<SoftwareSupportApplication, Integer> {
    Optional<SoftwareSupportApplication> findTopByOrderBySoftwareSupportApplicationIdDesc();
    SoftwareSupportApplication findSoftwareSupportApplicationBySoftwareSupportApplicationId(int id);

    List<SoftwareSupportApplication> findSoftwareSupportApplicationsByIsDeletedFalse();

    // Lấy tất cả các ứng dụng hỗ trợ phần mềm của Customer với isDeleted = false
    List<SoftwareSupportApplication> findByCustomerIsNotNullAndIsDeletedFalse();

    // Lấy tất cả các ứng dụng hỗ trợ phần mềm của Employee với isDeleted = false
    List<SoftwareSupportApplication> findByEmployeeIsNotNullAndIsDeletedFalse();
}
