package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.AccountForCustomer;
import com.hairsalonbookingapp.hairsalon.entity.AccountForEmployee;
import com.hairsalonbookingapp.hairsalon.exception.AccountBlockedException;
import com.hairsalonbookingapp.hairsalon.exception.AccountNotFoundException;
import com.hairsalonbookingapp.hairsalon.exception.DuplicateEntity;
import com.hairsalonbookingapp.hairsalon.model.*;
import com.hairsalonbookingapp.hairsalon.repository.CustomerRepository;
import com.hairsalonbookingapp.hairsalon.repository.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class AuthenticationService implements UserDetailsService{

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthenticationManager authenticationManager;

    //CHECK INPUT LÀ SĐT HAY NAME
    private boolean isPhoneNumber(String input) {
        // Logic để kiểm tra input có phải là số điện thoại
        return input.matches("(84|0[3|5|7|8|9])+([0-9]{8})\\b");
    }


    public AccountResponseForCustomer registerCustomer(RegisterRequestForCustomer registerRequestForCustomer){
        AccountForCustomer account = modelMapper.map(registerRequestForCustomer, AccountForCustomer.class);
        try{
            account.setScore(0);
            account.setCreatAt(new Date());
            account.setStatus(true);
            account.setDeleted(false);
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

    //LOGIN CUSTOMER
    public AccountResponseForCustomer loginForCustomer(LoginRequestForCustomer loginRequestForCustomer){
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequestForCustomer.getPhoneNumber(),
                    loginRequestForCustomer.getPassword()
            ));

            //=> tài khoản có tồn tại
            AccountForCustomer account = (AccountForCustomer) authentication.getPrincipal();
            if(account.isDeleted()){
                throw new AccountBlockedException("Your account is blocked!");
            } else {
                return modelMapper.map(account, AccountResponseForCustomer.class);
            }
        } catch (BadCredentialsException e) {
            throw new AccountNotFoundException("Phonenumber or password invalid!");
        }

    }

    public AccountResponseForEmployee registerEmployee(RegisterRequestForEmployee registerRequestForEmployee){
        AccountForEmployee account = modelMapper.map(registerRequestForEmployee, AccountForEmployee.class);
        try{
            account.setId("1");
            account.setBaseSalary(2);
            account.setCreatedAt(new Date());
            account.setStatus(true);
            String originPassword = account.getPassword();
            account.setPassword(passwordEncoder.encode(originPassword));
            AccountForEmployee newAccount = employeeRepository.save(account); //lưu xuống database
            return modelMapper.map(newAccount, AccountResponseForEmployee.class);
        } catch (Exception e) {
            if (e.getMessage().contains(account.getName())) {
                throw new DuplicateEntity("DuplicateEntity name!");
            } else {
                throw new DuplicateEntity("DuplicateEntity email!");
            }

        }
    }

    //LOGIN EMPLOYEE
    public AccountResponseForEmployee loginForEmployee(LoginRequestForEmployee loginRequestForEmployee){
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequestForEmployee.getName(),
                    loginRequestForEmployee.getPassword()
            ));

            //=> tài khoản có tồn tại
            AccountForEmployee account = (AccountForEmployee) authentication.getPrincipal();
            if(account.isDeleted()){
                throw new AccountBlockedException("Your account is blocked!");
            } else {
                return modelMapper.map(account, AccountResponseForEmployee.class);
            }
        } catch (BadCredentialsException e) { //lỗi này xuất hiện khi xác thực thất bại
            throw new AccountNotFoundException("Username or password invalid!");
        }

    }

    //GET PROFILE CUSTOMER
    public ProfileCustomer getProfileCustomer(LoginRequestForCustomer loginRequestForCustomer){
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequestForCustomer.getPhoneNumber(),
                    loginRequestForCustomer.getPassword()
            ));
            //=> tài khoản có tồn tại
            AccountForCustomer account = (AccountForCustomer) authentication.getPrincipal();
            if(account.isDeleted()==false){
                return modelMapper.map(account, ProfileCustomer.class);
            } else {
                throw new Exception("Account is blocked!");
            }
        } catch (Exception e) {
            throw new AccountNotFoundException("Can not find profile!");
        }
    }

    //GET PROFILE EMPLOYEE
    public ProfileEmployee getProfileEmployee(LoginRequestForEmployee loginRequestForEmployee){
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequestForEmployee.getName(),
                    loginRequestForEmployee.getPassword()
            ));

            //=> tài khoản có tồn tại
            AccountForEmployee account = (AccountForEmployee) authentication.getPrincipal();
            if(account.isDeleted()==false){
                return modelMapper.map(account, ProfileEmployee.class);
            } else {
                throw new Exception("Account is blocked!");
            }
        } catch (Exception e) {
            throw new AccountNotFoundException("Can not find profile!");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        if(isPhoneNumber(input)){
            return loadUserByPhoneNumber(input);
        } else {
            return loadUserByName(input);
        }
    }

    public UserDetails loadUserByPhoneNumber(String phoneNumber) throws UsernameNotFoundException {
        if(customerRepository.findAccountForCustomerByPhoneNumber(phoneNumber)!=null){
            return customerRepository.findAccountForCustomerByPhoneNumber(phoneNumber);
        } else {
            throw new AccountNotFoundException("Phonenumber or password invalid!");
        }
    }

    public UserDetails loadUserByName(String name) throws UsernameNotFoundException {
        if(employeeRepository.findAccountForEmployeeByName(name)!=null){
            return employeeRepository.findAccountForEmployeeByName(name);
        } else {
            throw new AccountNotFoundException("Username or password invalid!");
        }

    }
}
