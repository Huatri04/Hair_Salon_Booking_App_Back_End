package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.*;
import com.hairsalonbookingapp.hairsalon.exception.AccountNotFoundException;
import com.hairsalonbookingapp.hairsalon.exception.Duplicate;
import com.hairsalonbookingapp.hairsalon.exception.UpdatedException;
import com.hairsalonbookingapp.hairsalon.model.*;
import com.hairsalonbookingapp.hairsalon.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.annotation.CreatedBy;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.modelmapper.ModelMapper;

import java.util.Date;
import java.util.Optional;

@Service
@Transactional
public class AuthenticationService implements UserDetailsService {
    @Autowired
    AccountForCustomerRepository accountForCustomerRepository;

    @Autowired
    EmployeeRepository employeeRepository;


    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    TokenService tokenService;
    @Validated(CreatedBy.class)// phan vao nhom created
    // logic dang ki tk cho guest
    public AccountForCustomerResponse register(RegisterRequestForCustomer registerRequestForCustomer){
        AccountForCustomer account = modelMapper.map(registerRequestForCustomer, AccountForCustomer.class);
        try {
            String originPassword = account.getPassword();
            account.setPassword(passwordEncoder.encode(originPassword));
            AccountForCustomer newAccount = accountForCustomerRepository.save(account);
            account.setCreatAt(new Date());
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
//                String originPassword = account.getPassword();
//                if (account.getPassword() != null && !account.getPassword().isEmpty()) {
//                    oldAccount.setPassword(passwordEncoder.encode(originPassword));
//                }

                // Kiểm tra và cập nhật tên
                if (account.getName() != null && !account.getName().isEmpty()) {
                    oldAccount.setName(account.getName());
                }

                // Lưu cập nhật vào cơ sở dữ liệu
                AccountForCustomer updatedAccount = accountForCustomerRepository.save(oldAccount);
                return modelMapper.map(updatedAccount, EditProfileCustomerResponse.class);
            } catch (DataIntegrityViolationException e) {
                if(e.getMessage().contains(account.getEmail())){
                    throw new UpdatedException("duplicate email!");
                } else if (e.getMessage().contains(account.getPassword())) {
                    throw new UpdatedException("duplicate password!");
                }
            }
        }
        return null;
    }



    // logic update profile cho employee


    public EditProfileEmployeeResponse updatedAccount(RequestEditProfileEmployee requestEditProfileEmployee, String id) {
        AccountForEmployee account = modelMapper.map(requestEditProfileEmployee, AccountForEmployee.class);
            AccountForEmployee oldAccount = employeeRepository.findAccountForEmployeeByEmployeeId(id);
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
//                    String originPassword = account.getPassword();
//                    if (account.getPassword() != null && !account.getPassword().isEmpty()) {
//                        oldAccount.setPassword(passwordEncoder.encode(originPassword));
//                    }
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
                        throw new UpdatedException("duplicate email!");
                    } else if (e.getMessage().contains(account.getPhoneNumber())) {
                        throw new UpdatedException("duplicate phone!");
                    } else if (e.getMessage().contains(account.getPassword())) {
                        throw new UpdatedException("duplicate password!");
                    }
                }
            }
            return null;
    }

    public EditProfileEmployeeResponse updatedAccountByManager(RequestUpdateProfileEmployeeByManager requestUpdateProfileEmployeeByManager, String id) {
        AccountForEmployee account = modelMapper.map(requestUpdateProfileEmployeeByManager, AccountForEmployee.class);
        AccountForEmployee oldAccount = employeeRepository.findAccountForEmployeeByEmployeeId(id);
        if (oldAccount == null) {
            throw new Duplicate("Account not found!");// cho dung luon
        } else {
            try{
                if (account.getStylistLevel() != null && !account.getStylistLevel().isEmpty()) {
                    oldAccount.setStylistLevel(account.getStylistLevel());
                }

                if (account.getStylistSelectionFee() != 0 ) {
                    if(account.getStylistSelectionFee() < 0 ){
                        throw new Duplicate("Stylist Selection Fee must be at least 0");
                    }
                    oldAccount.setStylistSelectionFee(account.getStylistSelectionFee());
                }

                if (account.getKPI() != 0) {
                    if(account.getKPI() < 0 ){
                        throw new Duplicate("KPI must be at least 0");
                    }
                    oldAccount.setKPI(account.getKPI());
                }

                // Lưu cập nhật vào cơ sở dữ liệu
                AccountForEmployee updatedAccount = employeeRepository.save(oldAccount);
                return modelMapper.map(updatedAccount, EditProfileEmployeeResponse.class);
            } catch (Exception e) {
                throw new UpdatedException("employee can not update!");
            }
        }
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





    // logic dang ki tk cho employee
    @Validated(CreatedBy.class)// phan vao nhom created
    public AccountForEmployeeResponse register(RegisterRequestForEmloyee registerRequestForEmloyee) {
        AccountForEmployee account = modelMapper.map(registerRequestForEmloyee, AccountForEmployee.class);
        try {
//            account.setId(generateNewId());
//            return employeeRepository.save(account);

            // Tạo ID dựa trên vai trò
            String newId = generateIdBasedOnRole(account.getRole());
            account.setEmployeeId(newId);
            String originPassword = account.getPassword();
            account.setPassword(passwordEncoder.encode(originPassword));
            account.setCreatedAt(new Date());

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
        Optional<AccountForEmployee> lastAccount = employeeRepository.findTopByRoleOrderByEmployeeIdDesc(role);
        int newIdNumber = 1; // Mặc định bắt đầu từ 1

        // Nếu có tài khoản cuối cùng, lấy ID
        if (lastAccount.isPresent()) {
            String lastId = lastAccount.get().getEmployeeId();
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

    //CHECK INPUT LÀ SĐT HAY NAME
    public boolean isPhoneNumber(String input) {
        // Logic để kiểm tra input có phải là số điện thoại
        return input.matches("(84|0[3|5|7|8|9])+([0-9]{8})\\b");
    }




    //LOGIN CUSTOMER
    public AccountForCustomerResponse loginForCustomer(LoginRequestForCustomer loginRequestForCustomer){
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequestForCustomer.getPhoneNumber(),
                    loginRequestForCustomer.getPassword()
            ));

            //=> tài khoản có tồn tại
            AccountForCustomer account = (AccountForCustomer) authentication.getPrincipal();
            if(account.isDeleted()){
                throw new Duplicate("Your account is blocked!");
            } else {
                AccountForCustomerResponse accountResponseForCustomer = modelMapper.map(account, AccountForCustomerResponse.class);
                accountResponseForCustomer.setToken(tokenService.generateTokenCustomer(account));
                return accountResponseForCustomer;
            }
        } catch (BadCredentialsException e) {
            throw new AccountNotFoundException("Phonenumber or password invalid!");
        }

    }


    //LOGIN EMPLOYEE
    public AccountForEmployeeResponse loginForEmployee(LoginRequestForEmployee loginRequestForEmployee){
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequestForEmployee.getUsername(),
                    loginRequestForEmployee.getPassword()
            ));

            //=> tài khoản có tồn tại
            AccountForEmployee account = (AccountForEmployee) authentication.getPrincipal();
            if(account.isDeleted()){
                throw new Duplicate("Your account is blocked!");
            } else {
                AccountForEmployeeResponse accountResponseForEmployee = modelMapper.map(account, AccountForEmployeeResponse.class);
                accountResponseForEmployee.setToken(tokenService.generateTokenEmployee(account));
                return accountResponseForEmployee;
            }
        } catch (BadCredentialsException e) { //lỗi này xuất hiện khi xác thực thất bại
            throw new AccountNotFoundException("Username or password invalid!");
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
        if(accountForCustomerRepository.findByPhoneNumber(phoneNumber)!=null){
            return accountForCustomerRepository.findByPhoneNumber(phoneNumber);
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

//    public AccountForCustomer getCurrentAccountForCustomer(){
//        AccountForCustomer account = (AccountForCustomer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        return accountForCustomerRepository.findByPhoneNumber(account.getPhoneNumber());
//    }
//
//    public AccountForEmployee getCurrentAccountForEmployee(){
//        AccountForEmployee account = (AccountForEmployee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        return employeeRepository.findAccountForEmployeeByEmployeeId(account.getEmployeeId());
//    }

    public AccountForCustomer getCurrentAccountForCustomer() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Kiểm tra nếu principal là kiểu UserDetails và lấy thông tin
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            // Trích xuất thông tin từ UserDetails (username, phone number, etc.)
            String phoneNumber = userDetails.getUsername();  // hoặc dùng getPhoneNumber nếu có
            return accountForCustomerRepository.findByPhoneNumber(phoneNumber);
        } else {
            throw new ClassCastException("Current user is not a valid customer.");
        }
    }

    public AccountForEmployee getCurrentAccountForEmployee() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Kiểm tra nếu principal là kiểu UserDetails và lấy thông tin
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            // Trích xuất thông tin từ UserDetails (username, employee ID, etc.)
            String employeeId = userDetails.getUsername();  // hoặc dùng getEmployeeId nếu có
            return employeeRepository.findAccountForEmployeeByEmployeeId(employeeId);
        } else {
            throw new ClassCastException("Current user is not a valid employee.");
        }
    }

}
