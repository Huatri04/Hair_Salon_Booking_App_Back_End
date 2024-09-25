package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.*;
import com.hairsalonbookingapp.hairsalon.exception.AccountNotFoundException;
import com.hairsalonbookingapp.hairsalon.exception.DuplicateEntity;
import com.hairsalonbookingapp.hairsalon.model.AccountResponseForCustomer;
import com.hairsalonbookingapp.hairsalon.model.LoginRequestForCustomer;
import com.hairsalonbookingapp.hairsalon.model.RegisterRequestForCustomer;
import com.hairsalonbookingapp.hairsalon.repository.*;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

public class AuthenticationServiceForCustomer  {
/*implements UserDetailsService
    @Autowired
    CustomerRepository customerRepository;


    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthenticationManager authenticationManager;

    public AccountResponseForCustomer registerCustomer(RegisterRequestForCustomer registerRequestForCustomer){
        AccountForCustomer account = modelMapper.map(registerRequestForCustomer, AccountForCustomer.class);
        try{
            account.setScore(0);
            account.setCreatAt(new Date());
            account.setStatus(true);
            String originPassword = account.getPassword();
            account.setPassword(passwordEncoder.encode(originPassword));
            AccountForCustomer newAccount = customerRepository.save(account); //lưu xuống database
            return modelMapper.map(newAccount, AccountResponseForCustomer.class);
        } catch (Exception e) {
            if (e.getMessage().contains(account.getPhoneNumber())) {
                throw new DuplicateEntity("DuplicateEntity phone!");
            } else {
                throw new DuplicateEntity("DuplicateEntity email!");
            }

        }
    }

    public AccountResponseForCustomer loginForCustomer(LoginRequestForCustomer loginRequestForCustomer){
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequestForCustomer.getPhoneNumber(),
                    loginRequestForCustomer.getPassword()
            ));

            //=> tài khoản có tồn tại
            AccountForCustomer account = (AccountForCustomer) authentication.getPrincipal();
            return modelMapper.map(account, AccountResponseForCustomer.class);
        } catch (Exception e) {
            throw new AccountNotFoundException("Username or password invalid!");
        }

    }

    /*public AccountResponseForCustomer register(RegisterRequestForCustomer registerRequestForCustomer){
        AccountForCustomer account = modelMapper.map(registerRequestForCustomer, AccountForCustomer.class);
        try{
            String originPassword = account.getPassword();
            account.setPassword(passwordEncoder.encode(originPassword));
            account
            AccountForCustomer newAccount = customerRepository.save(account); //lưu xuống database
            return modelMapper.map(newAccount, AccountResponse.class);
            // SQLIntegrityConstraintViolationException
            //ConstraintViolationException
        } catch (Exception e) {
            if (e.getMessage().contains(account.getCode())) {
                throw new DuplicateEntity("DuplicateEntity code!");
            } else {
                throw new DuplicateEntity("DuplicateEntity email!");
            }

        }
    }


    // logic dang ki tk cho guest
    public AccountForCustomer register(AccountForCustomer account){
        try {
            AccountForCustomer newAccount = customerRepository.save(account);
            newAccount.setCreatAt(account.getCreatAt());
            return newAccount;
        } catch (Exception e) {
            if(e.getMessage().contains(account.getEmail())){
                throw new DuplicateEntity("duplicate email!");
            } else if (e.getMessage().contains(account.getPhoneNumber())) {
                throw new DuplicateEntity("duplicate phone!");
            } else if (e.getMessage().contains(account.getPassword())) {
                throw new DuplicateEntity("duplicate password!");
            }
        }
        return null;
    }

    // update profile cho customer
    public AccountForCustomer updatedAccount(AccountForCustomer account, String id){
        AccountForCustomer oldAccount = customerRepository.findByPhoneNumber(id);
        try {

            if(oldAccount == null){
                throw new DuplicateEntity("Account not found!");
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
                return customerRepository.save(oldAccount);
            }
        } catch (Exception e) {
            if(e.getMessage().contains(oldAccount.getEmail())){
                throw new DuplicateEntity("duplicate email!");
            } else if (e.getMessage().contains(oldAccount.getPhoneNumber())) {
                throw new DuplicateEntity("duplicate phone!");
            } else if (e.getMessage().contains(oldAccount.getPassword())) {
                throw new DuplicateEntity("duplicate password!");
            }
        }
        return null;
    }



    // logic update profile cho employee
    public AccountForEmployee updatedAccount(AccountForEmployee account, String id){

        try {
            AccountForEmployee oldAccount = employeeRepository.findEmployeeById(id);
            if(oldAccount == null){
                throw new DuplicateEntity("Account not found!");
            }else{
                //account ton tai
                if(account.getEmail() != null){
                    oldAccount.setEmail(account.getEmail());
                }
                if(account.getPhoneNumber() != null){
                    oldAccount.setPhoneNumber(account.getPhoneNumber());
                }
                if(account.getPassword() != null){
                    oldAccount.setPassword(account.getPassword());
                }
                if(account.getName() != null){
                    oldAccount.setName(account.getName());
                }
                if(account.getImg() != null){
                    oldAccount.setImg(account.getImg());
                }
                return employeeRepository.save(oldAccount);
            }
        } catch (Exception e) {
            if(e.getMessage().contains(account.getEmail())){
                throw new DuplicateEntity("duplicate email!");
            } else if (e.getMessage().contains(account.getPhoneNumber())) {
                throw new DuplicateEntity("duplicate phone!");
            } else if (e.getMessage().contains(account.getPassword())) {
                throw new DuplicateEntity("duplicate password!");
            }
        }
        return null;
    }



    // logic dang ki tk cho employee
    public AccountForEmployee register(AccountForEmployee account) {
        AccountForEmployee newAccount = null;
        try {
//            String id = generateIdBasedOnRole(account.getRole());
//            account.setId(id);
//            newAccount = employeeRepository.save(account);
//            return newAccount;

            account.setId(generateNewId());
            return employeeRepository.save(account);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains(account.getEmail())) {
                throw new DuplicateEntity("duplicate email!");
            } else if (e.getMessage().contains(account.getPhoneNumber())) {
                throw new DuplicateEntity("duplicate phone!");
            } else if (e.getMessage().contains(account.getPassword())) {
                throw new DuplicateEntity("duplicate password!");
            }
        }
        return null;
    }


    private String generateNewId() {
        Optional<AccountForEmployee> lastAccount = employeeRepository.findTopByOrderByIdDesc();
        if (lastAccount.isPresent()) {
            String lastId = lastAccount.get().getId();
            int idNum = Integer.parseInt(lastId.substring(1)) + 1; // Tăng giá trị số lên 1
            return String.format("E%06d", idNum); // Tạo ID mới
        }
        return "E000001"; // Nếu chưa có bản ghi nào, bắt đầu từ E000001
    }



    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        return customerRepository.findAccountForCustomerByPhoneNumber(phoneNumber);
    }*/
//    public String generateIdBasedOnRole(String role) {
//        String lastId = employeeRepository.findLastIdByRole(role);
//        String prefix = null;
//        switch (role) {
//            case "Stylist":
//                prefix = "STY";
//                break;
//            case "Staff":
//                prefix = "STA";
//                break;
//            case "Manager":
//                prefix = "MAN";
//                break;
//            case "Admin":
//                prefix = "ADM";
//                break;
//            default:
//                throw new IllegalArgumentException("Invalid role");
//        }
//
//        if (lastId == null) {
//            return prefix + "000001";
//        }
//
//        try {
//            int lastIdNumber = Integer.parseInt(lastId.replace(prefix, ""));
//            int newIdNumber = lastIdNumber + 1;
//            return prefix + String.format("%06d", newIdNumber);
//        } catch (NumberFormatException e) {
//            throw new DuplicateEntity("id can create!");
//        }
//    }





}
