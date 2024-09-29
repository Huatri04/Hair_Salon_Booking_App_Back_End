package com.hairsalonbookingapp.hairsalon.repository;

import com.hairsalonbookingapp.hairsalon.entity.AccountForEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<AccountForEmployee, String> {
    String findLastIdByRole(String role);

    // Lấy account có id lớn nhất để tạo id mới
    Optional<AccountForEmployee> findTopByOrderByIdDesc();

    AccountForEmployee findAccountForEmployeeByUsername(String username);
}
