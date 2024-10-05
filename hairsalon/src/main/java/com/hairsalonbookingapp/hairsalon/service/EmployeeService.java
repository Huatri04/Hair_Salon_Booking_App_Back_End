package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.AccountForEmployee;
import com.hairsalonbookingapp.hairsalon.exception.EntityNotFoundException;
import com.hairsalonbookingapp.hairsalon.model.EmployeeInfo;
import com.hairsalonbookingapp.hairsalon.model.FindEmployeeRequest;
import com.hairsalonbookingapp.hairsalon.repository.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    ModelMapper modelMapper;

    public List<EmployeeInfo> getEmployeeByRole(FindEmployeeRequest findEmployeeRequest){
        String status = "Workday";
        List<AccountForEmployee> accountForEmployeeList = new ArrayList<>();
        if(findEmployeeRequest.getRole().equals("Stylist")){
            if(findEmployeeRequest.getStylistLevel().equals("Normal")){
                accountForEmployeeList = employeeRepository.findAccountForEmployeesByRoleStylistAndStylistLevelNormalAndStatusWorkdayAndIsDeletedFalse();
            } else if(findEmployeeRequest.getStylistLevel().equals("Expert")){
                accountForEmployeeList = employeeRepository.findAccountForEmployeesByRoleStylistAndStylistLevelExpertAndStatusWorkdayAndIsDeletedFalse();
            } else {
                throw new EntityNotFoundException("Stylist not found!");
            }
        } else {
            accountForEmployeeList = employeeRepository.findAccountForEmployeesByRoleAndStatusAndIsDeletedFalse(findEmployeeRequest.getRole(), status);
        }

        if(accountForEmployeeList != null){
            List<EmployeeInfo> employeeInfoList = new ArrayList<>();
            for(AccountForEmployee accountForEmployee : accountForEmployeeList){
                EmployeeInfo employeeInfo = modelMapper.map(accountForEmployee, EmployeeInfo.class);
                employeeInfoList.add(employeeInfo);
            }

            return employeeInfoList;
        } else {
            throw new EntityNotFoundException("Employee not found!");
        }
    }





}
