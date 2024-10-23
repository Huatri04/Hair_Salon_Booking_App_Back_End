package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.AccountForCustomer;
import com.hairsalonbookingapp.hairsalon.model.response.CustomerAccountInfo;
import com.hairsalonbookingapp.hairsalon.repository.AccountForCustomerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {

    @Autowired
    AccountForCustomerRepository customerRepository;

    @Autowired
    ModelMapper modelMapper;

    // LẤY TOÀN BỘ CUSTOMER
    public List<CustomerAccountInfo> getAllCustomerAccounts(){
        List<AccountForCustomer> accountForCustomerList = customerRepository.findAll();
        List<CustomerAccountInfo> customerAccountInfoList = new ArrayList<>();
        for(AccountForCustomer accountForCustomer : accountForCustomerList){
            CustomerAccountInfo customerAccountInfo = modelMapper.map(accountForCustomer, CustomerAccountInfo.class);
            customerAccountInfoList.add(customerAccountInfo);
        }
        return customerAccountInfoList;
    }
}
