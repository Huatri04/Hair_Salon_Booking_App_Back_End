package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.*;
import com.hairsalonbookingapp.hairsalon.exception.Duplicate;
import com.hairsalonbookingapp.hairsalon.exception.UpdatedException;
import com.hairsalonbookingapp.hairsalon.model.*;
import com.hairsalonbookingapp.hairsalon.repository.*;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.modelmapper.ModelMapper;
import org.springframework.web.server.ResponseStatusException;

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

    public EditProfileCustomerResponse updatedAccount(RequestEditProfileCustomer requestEditProfileCustomer, String phone){
        AccountForCustomer account = modelMapper.map(requestEditProfileCustomer, AccountForCustomer.class);
        AccountForCustomer oldAccount = accountForCustomerRepository.findByPhoneNumber(phone);
        if (oldAccount == null) {
            throw new Duplicate("Account not found!");// cho dung luon
        } else {

            try{
                if (account.getEmail() != null && !account.getEmail().isEmpty()) {
                    oldAccount.setEmail(account.getEmail());
                }

                // Kiểm tra mật khẩu phải lớn hơn 6 ký tự
                String originPassword = account.getPassword();
                if (account.getPassword() != null && !account.getPassword().isEmpty()) {
                    oldAccount.setPassword(passwordEncoder.encode(originPassword));
                }

                // Kiểm tra và cập nhật tên
                if (account.getName() != null && !account.getName().isEmpty()) {
                    oldAccount.setName(account.getName());
                }

                // Lưu cập nhật vào cơ sở dữ liệu
                AccountForCustomer updatedAccount = accountForCustomerRepository.save(oldAccount);
                return modelMapper.map(updatedAccount, EditProfileCustomerResponse.class);
            } catch (DataIntegrityViolationException e) {
                if(e.getMessage().contains(account.getEmail())){
                    throw new Duplicate("duplicate email!");
                } else if (e.getMessage().contains(account.getPassword())) {
                    throw new Duplicate("duplicate password!");
                }
            }
        }
        return null;
    }

    @Autowired
    EmployeeRepository employeeRepository;

    // logic update profile cho employee


    public EditProfileEmployeeResponse updatedAccount(RequestEditProfileEmployee requestEditProfileEmployee, String id) {
        AccountForEmployee account = modelMapper.map(requestEditProfileEmployee, AccountForEmployee.class);
            AccountForEmployee oldAccount = employeeRepository.findEmployeeById(id);
            if (oldAccount == null) {
                throw new Duplicate("Account not found!");// cho dung luon
            } else {
                try{
                    if (account.getEmail() != null && !account.getEmail().isEmpty()) {
                        oldAccount.setEmail(account.getEmail());
                    }
                    // Kiểm tra số điện thoại hợp lệ (ví dụ: 10 chữ số)
                    if (account.getPhoneNumber() != null && !account.getPhoneNumber().isEmpty()) {
                        oldAccount.setPhoneNumber(account.getPhoneNumber());
                    }
                    // Kiểm tra mật khẩu phải lớn hơn 6 ký tự
                    String originPassword = account.getPassword();
                    if (account.getPassword() != null && !account.getPassword().isEmpty()) {
                        oldAccount.setPassword(passwordEncoder.encode(originPassword));
                    }
                    // Kiểm tra và cập nhật tên
                    if (account.getName() != null && !account.getName().isEmpty()) {
                        oldAccount.setName(account.getName());
                    }

                    // Kiểm tra và cập nhật ảnh
                    if (account.getImg() != null && !account.getImg().isEmpty()) {
                        oldAccount.setImg(account.getImg());
                    }

                    // Lưu cập nhật vào cơ sở dữ liệu
                    AccountForEmployee updatedAccount = employeeRepository.save(oldAccount);
                    return modelMapper.map(updatedAccount, EditProfileEmployeeResponse.class);
                } catch (Exception e) {
                    if(e.getMessage().contains(account.getEmail())){
                        throw new Duplicate("duplicate email!");
                    } else if (e.getMessage().contains(account.getPhoneNumber())) {
                        throw new Duplicate("duplicate phone!");
                    } else if (e.getMessage().contains(account.getPassword())) {
                        throw new Duplicate("duplicate password!");
                    }
                }
            }
            return null;
    }

//    public EditProfileEmployeeResponse updatedAccount(RequestEditProfileEmployee requestEditProfileEmployee, String id) {
//        AccountForEmployee account = modelMapper.map(requestEditProfileEmployee, AccountForEmployee.class);
//            AccountForEmployee oldAccount = employeeRepository.findEmployeeById(id);
//            if (oldAccount == null) {
//                throw new Duplicate("Account not found!");// cho dung luon
//            } else {
//                // Kiểm tra email hợp lệ
//                if (account.getEmail() != null && !account.getEmail().isEmpty()) {
//                    if (!account.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
//                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email invalid!");
//                    }
//                    oldAccount.setEmail(account.getEmail());
//                }
//
//                // Kiểm tra số điện thoại hợp lệ (ví dụ: 10 chữ số)
//                if (account.getPhoneNumber() != null && !account.getPhoneNumber().isEmpty()) {
//                    if (!account.getPhoneNumber().matches("^[0-9]{10}$")) {
//                        throw new UpdatedException("phone number invalid!");
//                    }
//                    oldAccount.setPhoneNumber(account.getPhoneNumber());
//                }
//
//                // Kiểm tra mật khẩu phải lớn hơn 6 ký tự
//                String originPassword = account.getPassword();
//                if (account.getPassword() != null && !account.getPassword().isEmpty()) {
//                    if (account.getPassword().length() <= 6) {
//                        throw new UpdatedException("Password must be at least 6 characters");
//                    }
//                    oldAccount.setPassword(passwordEncoder.encode(originPassword));
//                }
//
//                // Kiểm tra và cập nhật tên
//                if (account.getName() != null && !account.getName().isEmpty()) {
//                    oldAccount.setName(account.getName());
//                }
//
//                // Kiểm tra và cập nhật ảnh
//                if (account.getImg() != null && !account.getImg().isEmpty()) {
//                    oldAccount.setImg(account.getImg());
//                }
//
//                // Lưu cập nhật vào cơ sở dữ liệu
//                AccountForEmployee updatedAccount = employeeRepository.save(oldAccount);
//                return modelMapper.map(updatedAccount, EditProfileEmployeeResponse.class);
//            }
//    }

//    public EditProfileEmployeeResponse updatedAccount(RequestEditProfileEmployee requestEditProfileEmployee, String id){
//        AccountForEmployee account = modelMapper.map(requestEditProfileEmployee, AccountForEmployee.class);
//
//            AccountForEmployee oldAccount = employeeRepository.findEmployeeById(id);
//            if(oldAccount == null){
//                throw new Duplicate("Account not found!");
//            }else{
//                //account ton tai
//                if(account.getEmail() != null){
//                    try{
//                        oldAccount.setEmail(account.getEmail());
//                    } catch (Exception e) {
//                        if(e.getMessage().contains(account.getEmail())){
//                            throw new Duplicate("duplicate email!");
//                        }
//                    }
//                }else{
//                    oldAccount.setEmail(oldAccount.getEmail());
//                }
//                if(account.getPhoneNumber() != null){
//                    try{
//                        oldAccount.setPhoneNumber(account.getPhoneNumber());
//                    } catch (Exception e) {
//                        if(e.getMessage().contains(account.getPhoneNumber())){
//                            throw new Duplicate("duplicate phone number!");
//                        }
//                    }
//                }else{
//                    oldAccount.setPhoneNumber(oldAccount.getPhoneNumber());
//                }
//                if(account.getPassword() != null){
//                    try{
//                        oldAccount.setPassword(account.getPassword());
//                    } catch (Exception e) {
//                        if(e.getMessage().contains(account.getPassword())){
//                            throw new Duplicate("duplicate pasword!");
//                        }
//                    }
//                }else{
//                    oldAccount.setPassword(oldAccount.getPassword());
//                }
//                if(account.getName() != null){
//                    try{
//                        oldAccount.setName(account.getName());
//                    } catch (Exception e) {
//                        if(e.getMessage().contains(account.getName())){
//                            throw new Duplicate("duplicate name!");
//                        }
//                    }
//                }else{
//                    oldAccount.setName(oldAccount.getName());
//                }
//                if(account.getImg() != null){
//                    oldAccount.setImg(account.getImg());
//                }else{
//                    oldAccount.setImg(oldAccount.getImg());
//                }
//
//                AccountForEmployee oldAccount1 = employeeRepository.save(oldAccount);
//                return modelMapper.map(oldAccount1, EditProfileEmployeeResponse.class);
//            }
//    }



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
