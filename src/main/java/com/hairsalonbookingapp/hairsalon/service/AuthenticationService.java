package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.*;
import com.hairsalonbookingapp.hairsalon.exception.Duplicate;
import com.hairsalonbookingapp.hairsalon.model.AccountForCustomerResponse;
import com.hairsalonbookingapp.hairsalon.model.AccountForEmployeeResponse;
import com.hairsalonbookingapp.hairsalon.model.RegisterRequestForCustomer;
import com.hairsalonbookingapp.hairsalon.model.RegisterRequestForEmloyee;
import com.hairsalonbookingapp.hairsalon.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.modelmapper.ModelMapper;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AuthenticationService implements UserDetailsService {
    @Autowired
    AccountForCustomerRepository accountForCustomerRepository;


    @Validated(CreatedBy.class)// phan vao nhom created
    // logic dang ki tk cho guest
    public AccountForCustomerResponse register(RegisterRequestForCustomer registerRequestForCustomer){
        AccountForCustomer account = modelMapper.map(registerRequestForCustomer, AccountForCustomer.class);
        try {
            String originPassword = account.getPassword();
            account.setPassword(passwordEncoder.encode(originPassword));
            AccountForCustomer newAccount = accountForCustomerRepository.save(account);
            newAccount.setCreatAt(account.getCreatAt());

            return modelMapper.map(newAccount, AccountForCustomerResponse.class);
        } catch (Exception e) {
            if(e.getMessage().contains(account.getEmail())){
                throw new Duplicate("duplicate email!");
            } else if (e.getMessage().contains(account.getPhoneNumber())) {
                throw new Duplicate("duplicate phone!");
            } else if (e.getMessage().contains(account.getPassword())) {
                throw new Duplicate("duplicate password!");
            }
        }
        return null;
    }

    // update profile cho customer
    public AccountForCustomer updatedAccount(AccountForCustomer account, String id){
        AccountForCustomer oldAccount = accountForCustomerRepository.findByPhoneNumber(id);
        try {

            if(oldAccount == null){
                throw new Duplicate("Account not found!");
            }else{
                //account ton tai
                if(account.getEmail() != null){
                    oldAccount.setEmail(account.getEmail());
                }
                if(account.getPassword() != null){
                    oldAccount.setPassword(account.getPassword());
                }
                if(account.getName() != null){
                    oldAccount.setName(account.getName());
                }
                return accountForCustomerRepository.save(oldAccount);
            }
        } catch (Exception e) {
            if(e.getMessage().contains(oldAccount.getEmail())){
                throw new Duplicate("duplicate email!");
            } else if (e.getMessage().contains(oldAccount.getPhoneNumber())) {
                throw new Duplicate("duplicate phone!");
            } else if (e.getMessage().contains(oldAccount.getPassword())) {
                throw new Duplicate("duplicate password!");
            }
        }
        return null;
    }

    @Autowired
    EmployeeRepository employeeRepository;

    // logic update profile cho employee
    public AccountForEmployee updatedAccount(AccountForEmployee account, String id){
        try {
            AccountForEmployee oldAccount = employeeRepository.findEmployeeById(id);
            if(oldAccount == null){
                throw new Duplicate("Account not found!");
            }else{
                //account ton tai
                if(account.getEmail() != null){
                    oldAccount.setEmail(account.getEmail());
                }else{
                    oldAccount.setEmail(oldAccount.getEmail());
                }
                if(account.getPhoneNumber() != null){
                    oldAccount.setPhoneNumber(account.getPhoneNumber());
                }else{
                    oldAccount.setPhoneNumber(oldAccount.getPhoneNumber());
                }
                if(account.getPassword() != null){
                    oldAccount.setPassword(account.getPassword());
                }else{
                    oldAccount.setPassword(oldAccount.getPassword());
                }
                if(account.getName() != null){
                    oldAccount.setName(account.getName());
                }else{
                    oldAccount.setName(oldAccount.getName());
                }
                if(account.getImg() != null){
                    oldAccount.setImg(account.getImg());
                }else{
                    oldAccount.setImg(oldAccount.getImg());
                }
                return employeeRepository.save(oldAccount);
            }
        } catch (Exception e) {
            if(e.getMessage().contains(account.getEmail())){
                throw new Duplicate("duplicate email!");
            } else if (e.getMessage().contains(account.getPhoneNumber())) {
                throw new Duplicate("duplicate phone!");
            } else if (e.getMessage().contains(account.getPassword())) {
                throw new Duplicate("duplicate password!");
            }
        }
        return null;
    }



    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ModelMapper modelMapper;

    // logic dang ki tk cho employee
    @Validated(CreatedBy.class)// phan vao nhom created
    public AccountForEmployeeResponse register(RegisterRequestForEmloyee registerRequestForEmloyee) {
        AccountForEmployee account = modelMapper.map(registerRequestForEmloyee, AccountForEmployee.class);
        try {
//            account.setId(generateNewId());
//            return employeeRepository.save(account);

            // Tạo ID dựa trên vai trò
            String newId = generateIdBasedOnRole(account.getRole());
            account.setId(newId);
            String originPassword = account.getPassword();
            account.setPassword(passwordEncoder.encode(originPassword));

            AccountForEmployee newAccount = employeeRepository.save(account);


            return modelMapper.map(newAccount, AccountForEmployeeResponse.class);

        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains(account.getEmail())) {
                throw new Duplicate("duplicate email!");
            } else if (e.getMessage().contains(account.getPhoneNumber())) {
                throw new Duplicate("duplicate phone!");
            } else if (e.getMessage().contains(account.getPassword())) {
                throw new Duplicate("duplicate password!");
            } else if (e.getMessage().contains(account.getUsername())) {
                throw new Duplicate("duplicate Username!");
            }
        }
        return null;
    }

//    private String generateNewId() {
//        Optional<AccountForEmployee> lastAccount = employeeRepository.findTopByOrderByIdDesc();
//        if (lastAccount.isPresent()) {
//            String lastId = lastAccount.get().getId();
//            int idNum = Integer.parseInt(lastId.substring(1)) + 1; // Tăng giá trị số lên 1
//            return String.format("E%06d", idNum); // Tạo ID mới
//        }
//        return "E000001"; // Nếu chưa có bản ghi nào, bắt đầu từ E000001
//    }


    public String generateIdBasedOnRole(String role) {
        // Tìm ID cuối cùng theo vai trò
        Optional<AccountForEmployee> lastAccount = employeeRepository.findTopByRoleOrderByIdDesc(role);
        int newIdNumber = 1; // Mặc định bắt đầu từ 1

        // Nếu có tài khoản cuối cùng, lấy ID
        if (lastAccount.isPresent()) {
            String lastId = lastAccount.get().getId();
            newIdNumber = Integer.parseInt(lastId.replaceAll("\\D+", "")) + 1; // Tăng số lên 1
        }

        // Tạo ID mới dựa trên vai trò
        String prefix;
        switch (role) {
            case "Stylist":
                prefix = "STY";
                break;
            case "Staff":
                prefix = "STA";
                break;
            case "Manager":
                prefix = "MAN";
                break;
            case "Admin":
                prefix = "ADM";
                break;
            default:
                throw new IllegalArgumentException("Invalid role");
        }

        return String.format("%s%06d", prefix, newIdNumber); // Tạo ID mới với format
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
