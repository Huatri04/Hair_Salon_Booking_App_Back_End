package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.AccountForEmployee;
import com.hairsalonbookingapp.hairsalon.exception.EntityNotFoundException;
import com.hairsalonbookingapp.hairsalon.model.EmployeeInfo;
import com.hairsalonbookingapp.hairsalon.model.EmployeeResponsePage;
import com.hairsalonbookingapp.hairsalon.model.FindEmployeeRequest;
import com.hairsalonbookingapp.hairsalon.model.StylistInfo;
import com.hairsalonbookingapp.hairsalon.repository.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    ModelMapper modelMapper;

    public EmployeeResponsePage getEmployeeByRole(FindEmployeeRequest findEmployeeRequest, int page, int size){
        /*String status = "Workday";
        List<AccountForEmployee> accountForEmployeeList = new ArrayList<>();
        if(findEmployeeRequest.getRole().equals("Stylist")){
            if(findEmployeeRequest.getStylistLevel().equals("Normal")){
                accountForEmployeeList = employeeRepository.findAccountForEmployeesByRoleAndStylistLevelAndStatusAndIsDeletedFalse("Stylist", "Normal", status);
            } else if(findEmployeeRequest.getStylistLevel().equals("Expert")){
                accountForEmployeeList = employeeRepository.findAccountForEmployeesByRoleAndStylistLevelAndStatusAndIsDeletedFalse("Stylist", "Expert", status);
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
        }*/
        String status = "Workday";
        String role = findEmployeeRequest.getRole();
        String stylistLevel = "NotStylist";
        if(role.equals("Stylist")){
            stylistLevel = findEmployeeRequest.getStylistLevel();
            if(stylistLevel.isEmpty()){
                throw new EntityNotFoundException("Stylist level must not be blank!");
            }
        }

        Page<AccountForEmployee> accountForEmployeePage = employeeRepository
                .findAccountForEmployeesByRoleAndStylistLevelAndStatusAndIsDeletedFalse(
                        role,
                        stylistLevel,
                        status,
                        PageRequest.of(page, size));
        if(accountForEmployeePage.getContent().isEmpty()){
            throw new EntityNotFoundException("Employee not found!");
        }
        List<EmployeeInfo> employeeInfoList = new ArrayList<>();
        for(AccountForEmployee accountForEmployee : accountForEmployeePage.getContent()){
            EmployeeInfo employeeInfo = modelMapper.map(accountForEmployee, EmployeeInfo.class);
            employeeInfoList.add(employeeInfo);
        }
        EmployeeResponsePage employeeResponsePage = new EmployeeResponsePage();
        employeeResponsePage.setContent(employeeInfoList);
        employeeResponsePage.setPageNumber(accountForEmployeePage.getNumber());
        employeeResponsePage.setTotalPages(accountForEmployeePage.getTotalPages());
        employeeResponsePage.setTotalElements(accountForEmployeePage.getTotalElements());

        return employeeResponsePage;

    }


    //GET ALL STYLIST
    public List<EmployeeInfo> getAllAvailableStylist(){
        String role = "Stylist";
        String status = "Workday";
        List<EmployeeInfo> employeeInfoList = new ArrayList<>();
        List<AccountForEmployee> list = employeeRepository.findAccountForEmployeesByRoleAndStatusAndIsDeletedFalse(role, status);
        if(!list.isEmpty()){
            for(AccountForEmployee account : list){
                EmployeeInfo employeeInfo = modelMapper.map(account, EmployeeInfo.class);
                employeeInfoList.add(employeeInfo);
            }
            return employeeInfoList;
        } else {
            throw new EntityNotFoundException("Stylist not found!");
        }
    }

    // HÀM LẤY STYLIST
    public AccountForEmployee getStylist(String stylistId) {
        String status = "Workday";
        AccountForEmployee account = employeeRepository
                .findAccountForEmployeeByIdAndStatusAndIsDeletedFalse(stylistId, status);
        if(account != null){
            return account;
        } else {
            throw new EntityNotFoundException("Stylist not found!");
        }
    }



    /*// CUSTOMER NHỜ HỆ THỐNG CHỌN GIÚP STYLIST [TỰ ĐỘNG]
    public StylistInfo generateRandomStylist() {
        List<StylistInfo> stylistInfoList = getAllAvailableStylist();
        Random random = new Random();
        int randomIndex = random.nextInt(stylistInfoList.size());
        return stylistInfoList.get(randomIndex);
    }*/

    // HÀM LẤY TOÀN BỘ EMPLOYEE KHÔNG QUAN TRỌNG ROLE LÀ GÌ
    /*public List<EmployeeInfo> getAllEmployees(){
        List<AccountForEmployee> accountForEmployeeList = employeeRepository.findAccountForEmployeesByIsDeletedFalse();
        if(accountForEmployeeList.isEmpty()){
            throw new EntityNotFoundException("Employee not found!");
        }
        List<EmployeeInfo> employeeInfoList = new ArrayList<>();
        for(AccountForEmployee accountForEmployee : accountForEmployeeList){
            EmployeeInfo employeeInfo = modelMapper.map(accountForEmployee, EmployeeInfo.class);
            employeeInfoList.add(employeeInfo);
        }
        return employeeInfoList;
    }*/
    public EmployeeResponsePage getAllEmployees(int page, int size){
        Page<AccountForEmployee> accountForEmployeePage = employeeRepository.findAccountForEmployeesByIsDeletedFalse(PageRequest.of(page, size));
        List<EmployeeInfo> employeeInfoList = new ArrayList<>();
        for(AccountForEmployee accountForEmployee : accountForEmployeePage.getContent()){
            EmployeeInfo employeeInfo = modelMapper.map(accountForEmployee, EmployeeInfo.class);
            employeeInfoList.add(employeeInfo);
        }
        EmployeeResponsePage employeeResponsePage = new EmployeeResponsePage();
        employeeResponsePage.setContent(employeeInfoList);
        employeeResponsePage.setTotalPages(accountForEmployeePage.getTotalPages());
        employeeResponsePage.setTotalElements(accountForEmployeePage.getTotalElements());
        employeeResponsePage.setPageNumber(accountForEmployeePage.getNumber());
        return employeeResponsePage;
    }



    //HÀM LẤY TOÀN BỘ TÀI KHOẢN EMPLOYEE BỊ BAN/DELETED
    /*public List<EmployeeInfo> getAllBanedEmployees(){
        List<AccountForEmployee> accountForEmployeeList = employeeRepository.findAccountForEmployeesByIsDeletedTrue();
        if(accountForEmployeeList.isEmpty()){
            throw new EntityNotFoundException("Employee not found!");
        }
        List<EmployeeInfo> employeeInfoList = new ArrayList<>();
        for(AccountForEmployee accountForEmployee : accountForEmployeeList){
            EmployeeInfo employeeInfo = modelMapper.map(accountForEmployee, EmployeeInfo.class);
            employeeInfoList.add(employeeInfo);
        }
        return employeeInfoList;
    }*/
    public EmployeeResponsePage getAllBanedEmployees(int page, int size){
        Page<AccountForEmployee> accountForEmployeePage = employeeRepository.findAccountForEmployeesByIsDeletedTrue(PageRequest.of(page, size));
        List<EmployeeInfo> employeeInfoList = new ArrayList<>();
        for(AccountForEmployee accountForEmployee : accountForEmployeePage.getContent()){
            EmployeeInfo employeeInfo = modelMapper.map(accountForEmployee, EmployeeInfo.class);
            employeeInfoList.add(employeeInfo);
        }
        EmployeeResponsePage employeeResponsePage = new EmployeeResponsePage();
        employeeResponsePage.setContent(employeeInfoList);
        employeeResponsePage.setTotalPages(accountForEmployeePage.getTotalPages());
        employeeResponsePage.setTotalElements(accountForEmployeePage.getTotalElements());
        employeeResponsePage.setPageNumber(accountForEmployeePage.getNumber());
        return employeeResponsePage;
    }



    //HÀM CHECK DANH SÁCH CÁC STYLIST COI CÓ AI CHƯA SET NGÀY LÀM VIỆC KHÔNG
    public List<String> getStylistsThatWorkDaysNull(){
        String role = "Stylist";
        String status = "Workday";
        List<String> foundStylists = new ArrayList<>(); // DANH SÁCH CÁC STYLIST BẮT ĐƯỢC
        List<AccountForEmployee> allStylists = employeeRepository
                .findAccountForEmployeesByRoleAndStatusAndIsDeletedFalse(role, status);
        if(allStylists.isEmpty()){
            throw new EntityNotFoundException("Stylist not found!");
        }
        for(AccountForEmployee accountForEmployee : allStylists){
            if(accountForEmployee.getDays() == null){
                String foundStylist = "Id: " + accountForEmployee.getId() + ", Name: " + accountForEmployee.getName();
                foundStylists.add(foundStylist);
            }
        }
        return foundStylists;
    }


    //HÀM RESTART ACCOUNT EMPLOYEE
    public EmployeeInfo restartEmployee(String id){
        AccountForEmployee accountForEmployee = employeeRepository.findAccountForEmployeeById(id);
        if(accountForEmployee == null){
            throw new EntityNotFoundException("Employee not found!");
        }

        accountForEmployee.setDeleted(false);
        AccountForEmployee restartedAccount = employeeRepository.save(accountForEmployee);
        EmployeeInfo employeeInfo = modelMapper.map(restartedAccount, EmployeeInfo.class);
        return employeeInfo;
    }



}
