package com.hairsalonbookingapp.hairsalon.repository;

import com.hairsalonbookingapp.hairsalon.entity.AccountForEmployee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<AccountForEmployee, String> {
    //String findLastIdByRole(String role);

    // Lấy account có id lớn nhất để tạo id mới
    //Optional<AccountForEmployee> findTopByOrderByIdDesc();

    AccountForEmployee findAccountForEmployeeByUsername(String username);
    AccountForEmployee findAccountForEmployeeByIdAndStatusAndIsDeletedFalse(String id, String status);
    List<AccountForEmployee> findAccountForEmployeesByRoleAndStatusAndIsDeletedFalse(String role, String status);
    //List<AccountForEmployee> findAccountForEmployeesByRoleAndStatusAndIsDeletedFalse(String role, String status);
    List<AccountForEmployee> findAccountForEmployeesByRoleAndStylistLevelAndStatusAndIsDeletedFalse(String role, String stylistLevel, String status);
    //List<AccountForEmployee> findAccountForEmployeesByRoleStylistAndStylistLevelExpertAndStatusWorkdayAndIsDeletedFalse();
    //List<AccountForEmployee> findAccountForEmployeeByRoleStaffAndStatusWorkdayAndIsDeletedFalse();
    //List<AccountForEmployee> findAccountForEmployeeByRoleAdminAndStatusWorkdayAndIsDeletedFalse();
    AccountForEmployee findAccountForEmployeeById(String id);
    Page<AccountForEmployee> findAccountForEmployeesByRoleAndStylistLevelAndStatusAndIsDeletedFalse(String role, String stylistLevel, String status, Pageable pageable);
}
