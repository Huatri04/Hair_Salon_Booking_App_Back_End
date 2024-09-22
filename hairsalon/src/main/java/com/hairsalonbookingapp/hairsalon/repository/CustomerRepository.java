package com.hairsalonbookingapp.hairsalon.repository;

import com.hairsalonbookingapp.hairsalon.entity.AccountForCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<AccountForCustomer, String> {
    // tim 1 account bang id cua no
    //VD:  find + Student + By + Id(long id)
    AccountForCustomer findAccountForCustomerByPhoneNumber(String phoneNumber);


}
