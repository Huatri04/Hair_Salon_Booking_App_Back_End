package com.hairsalonbookingapp.hairsalon.repository;

import com.hairsalonbookingapp.hairsalon.entity.AccountForEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<AccountForEmployee, String> {

    Optional<String> findLastIdByRole(@Param("role") String role);
    Optional<AccountForEmployee> findTopByRoleOrderByEmployeeIdDesc(@Param("role") String role);

    // Lấy account có id lớn nhất để tạo id mới
    Optional<AccountForEmployee> findTopByOrderByEmployeeIdDesc();
    AccountForEmployee findAccountForEmployeeByUsername(String username);
    AccountForEmployee findAccountForEmployeeByEmployeeId(String id);
    AccountForEmployee findAccountForEmployeeByName(String name);
    List<AccountForEmployee> findAccountForEmployeesByIsDeletedFalse();
}
