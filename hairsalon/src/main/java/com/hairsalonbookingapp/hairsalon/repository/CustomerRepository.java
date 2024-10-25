package com.hairsalonbookingapp.hairsalon.repository;

import com.hairsalonbookingapp.hairsalon.entity.AccountForCustomer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<AccountForCustomer, String> {
    // tim 1 account bang id cua no
    //VD:  find + Student + By + Id(long id)
    AccountForCustomer findAccountForCustomerByPhoneNumber(String phoneNumber);
    Page<AccountForCustomer> findAll(Pageable pageable);
    List<AccountForCustomer> findAccountForCustomersByIsDeletedTrue();
    Page<AccountForCustomer> findAccountForCustomersByIsDeletedFalse(Pageable pageable);
    Page<AccountForCustomer> findAccountForCustomersByIsDeletedTrue(Pageable pageable);
}
