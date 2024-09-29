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
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    TokenService tokenService;

    //CHECK INPUT LÀ SĐT HAY NAME
    public boolean isPhoneNumber(String input) {
        // Logic để kiểm tra input có phải là số điện thoại
        return input.matches("(84|0[3|5|7|8|9])+([0-9]{8})\\b");
    }


    public AccountResponseForCustomer registerCustomer(RegisterRequestForCustomer registerRequestForCustomer){
        AccountForCustomer account = modelMapper.map(registerRequestForCustomer, AccountForCustomer.class);
        try{
            account.setPoint(0);
            account.setCreatAt(new Date());
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
                AccountResponseForCustomer accountResponseForCustomer = modelMapper.map(account, AccountResponseForCustomer.class);
                accountResponseForCustomer.setToken(tokenService.generateTokenCustomer(account));
                return accountResponseForCustomer;
            }
        } catch (BadCredentialsException e) {
            throw new AccountNotFoundException("Phonenumber or password invalid!");
        }

    }

    public AccountResponseForEmployee registerEmployee(RegisterRequestForEmployee registerRequestForEmployee){
        AccountForEmployee account = modelMapper.map(registerRequestForEmployee, AccountForEmployee.class);
        try{
            account.setId("1");
            account.setCreatedAt(new Date());
            account.setBasicSalary(500000);
            account.setStatus("Workday");
            //account.setDeleted(true);
            if(account.getRole().equals("Stylist")){
                account.setBasicSalary(600000);
                account.setKPI(60);
                if(account.getStylistLevel().equals("Expert")){
                    account.setBasicSalary(700000);
                    account.setExpertStylistBonus("20%");
                }
            }
            String originPassword = account.getPassword();
            account.setPassword(passwordEncoder.encode(originPassword));
            AccountForEmployee newAccount = employeeRepository.save(account); //lưu xuống database
            return modelMapper.map(newAccount, AccountResponseForEmployee.class);
        } catch (Exception e) {
            if (e.getMessage().contains(account.getUsername())) {
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
                    loginRequestForEmployee.getUsername(),
                    loginRequestForEmployee.getPassword()
            ));

            //=> tài khoản có tồn tại
            AccountForEmployee account = (AccountForEmployee) authentication.getPrincipal();
            if(account.isDeleted()){
                throw new AccountBlockedException("Your account is blocked!");
            } else {
                AccountResponseForEmployee accountResponseForEmployee = modelMapper.map(account, AccountResponseForEmployee.class);
                accountResponseForEmployee.setToken(tokenService.generateTokenEmployee(account));
                return accountResponseForEmployee;
            }
        } catch (BadCredentialsException e) { //lỗi này xuất hiện khi xác thực thất bại
            throw new AccountNotFoundException("Username or password invalid!");
        }

    }

    //GET PROFILE CUSTOMER
    public ProfileCustomer getProfileCustomer(){
        AccountForCustomer accountForCustomer = getCurrentAccountForCustomer();
        return modelMapper.map(accountForCustomer, ProfileCustomer.class);
        /*try{
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
        }*/
    }

    //GET PROFILE EMPLOYEE
    public ProfileEmployee getProfileEmployee(){
        AccountForEmployee accountForEmployee = getCurrentAccountForEmployee();
        return modelMapper.map(accountForEmployee, ProfileEmployee.class);
        /*try{
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
        }*/
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

    public UserDetails loadUserByName(String username) throws UsernameNotFoundException {
        if(employeeRepository.findAccountForEmployeeByUsername(username)!=null){
            return employeeRepository.findAccountForEmployeeByUsername(username);
        } else {
            throw new AccountNotFoundException("Username or password invalid!");
        }

    }

    public AccountForCustomer getCurrentAccountForCustomer(){
        AccountForCustomer account = (AccountForCustomer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return customerRepository.findAccountForCustomerByPhoneNumber(account.getPhoneNumber());
    }

    public AccountForEmployee getCurrentAccountForEmployee(){
        AccountForEmployee account = (AccountForEmployee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return employeeRepository.findAccountForEmployeeByUsername(account.getUsername());
    }
}
