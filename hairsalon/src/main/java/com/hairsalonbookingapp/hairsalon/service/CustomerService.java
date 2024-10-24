package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.AccountForCustomer;
import com.hairsalonbookingapp.hairsalon.model.CustomerAccountInfo;
import com.hairsalonbookingapp.hairsalon.model.CustomerResponsePage;
import com.hairsalonbookingapp.hairsalon.repository.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ModelMapper modelMapper;

    // LẤY TOÀN BỘ CUSTOMER
    /*public List<CustomerAccountInfo> getAllCustomerAccounts(){
        List<AccountForCustomer> accountForCustomerList = customerRepository.findAll();
        List<CustomerAccountInfo> customerAccountInfoList = new ArrayList<>();
        for(AccountForCustomer accountForCustomer : accountForCustomerList){
            CustomerAccountInfo customerAccountInfo = modelMapper.map(accountForCustomer, CustomerAccountInfo.class);
            customerAccountInfoList.add(customerAccountInfo);
        }
        return customerAccountInfoList;
    }*/
    public CustomerResponsePage getAllCustomerAccounts(int page, int size){
        Page<AccountForCustomer> accountForCustomerPage = customerRepository.findAccountForCustomersByIsDeletedFalse(PageRequest.of(page, size));
        List<CustomerAccountInfo> customerAccountInfoList = new ArrayList<>();
        for(AccountForCustomer accountForCustomer : accountForCustomerPage.getContent()){
            CustomerAccountInfo customerAccountInfo = modelMapper.map(accountForCustomer, CustomerAccountInfo.class);
            customerAccountInfoList.add(customerAccountInfo);
        }
        CustomerResponsePage customerResponsePage = new CustomerResponsePage();
        customerResponsePage.setContent(customerAccountInfoList);
        customerResponsePage.setTotalPages(accountForCustomerPage.getTotalPages());
        customerResponsePage.setTotalElements(accountForCustomerPage.getTotalElements());
        customerResponsePage.setPageNumber(accountForCustomerPage.getNumber());
        return customerResponsePage;
    }

    //HÀM LẤY CUSTOMER ACCOUNT BỊ BAN
    public List<CustomerAccountInfo> getAllBanedCustomerAccounts(){
        List<AccountForCustomer> accountForCustomerList = customerRepository.findAccountForCustomersByIsDeletedTrue();
        List<CustomerAccountInfo> customerAccountInfoList = new ArrayList<>();
        for(AccountForCustomer accountForCustomer : accountForCustomerList){
            CustomerAccountInfo customerAccountInfo = modelMapper.map(accountForCustomer, CustomerAccountInfo.class);
            customerAccountInfoList.add(customerAccountInfo);
        }
        return customerAccountInfoList;
    }
}
