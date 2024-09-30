package com.hairsalonbookingapp.hairsalon.repository;

import com.hairsalonbookingapp.hairsalon.entity.AccountForCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountForCustomerRepository extends JpaRepository<AccountForCustomer, String> {
    // tim 1 account bang id cua no
    //VD:  find + Student + By + Id(long id)
    AccountForCustomer findByPhoneNumber(String phoneNumber);
    List<AccountForCustomer> findAccountForCustomersByIsDeletedFalse();

}
