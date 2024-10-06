package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.*;
import com.hairsalonbookingapp.hairsalon.exception.AccountBlockedException;
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
import org.springframework.util.StringUtils;
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
//        AccountForCustomer account = modelMapper.map(requestEditProfileCustomer, AccountForCustomer.class);
        AccountForCustomer oldAccount = accountForCustomerRepository.findByPhoneNumber(phone);
        if (oldAccount == null) {
            throw new Duplicate("Account not found!");// cho dung luon
        } else {

            try{
//                if (account.getEmail() != null && !account.getEmail().isEmpty()) {
//                    oldAccount.setEmail(account.getEmail());
//                }
//
//                // Kiểm tra mật khẩu phải lớn hơn 6 ký tự
////                String originPassword = account.getPassword();
////                if (account.getPassword() != null && !account.getPassword().isEmpty()) {
////                    oldAccount.setPassword(passwordEncoder.encode(originPassword));
////                }
//
//                // Kiểm tra và cập nhật tên
//                if (account.getName() != null && !account.getName().isEmpty()) {
//                    oldAccount.setName(account.getName());
//                }
//
//                // Xử lý đổi mật khẩu
//                String oldPassword = requestEditProfileCustomer.getOldPassword();
//                String newPassword = requestEditProfileCustomer.getNewPassword();
//
//                if (StringUtils.hasText(newPassword) || StringUtils.hasText(oldPassword)) {
//                    // Nếu muốn đổi mật khẩu, cả hai trường phải được điền
//                    if (!StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
//                        throw new UpdatedException("Cần nhập cả mật khẩu cũ và mật khẩu mới để đổi mật khẩu.");
//                    }
//
//                    // Kiểm tra mật khẩu cũ
//                    if (!passwordEncoder.matches(oldPassword, oldAccount.getPassword())) {
//                        throw new UpdatedException("Mật khẩu cũ không chính xác.");
//                    }
//
//                    // Kiểm tra độ dài mật khẩu mới (đã được kiểm tra qua annotation @Size)
//                    // Mã hóa mật khẩu mới và cập nhật
//                    String encodedNewPassword = passwordEncoder.encode(newPassword);
//                    oldAccount.setPassword(encodedNewPassword);
//                }
//
//                // Lưu cập nhật vào cơ sở dữ liệu
//                AccountForCustomer updatedAccount = accountForCustomerRepository.save(oldAccount);
//                return modelMapper.map(updatedAccount, EditProfileCustomerResponse.class);


// vi model mapper ko nhan biet dc old vs new password nen lam ntn
                // Cập nhật email
                if (requestEditProfileCustomer.getEmail() != null && !requestEditProfileCustomer.getEmail().isEmpty()) {
                    oldAccount.setEmail(requestEditProfileCustomer.getEmail());
                }

                // Cập nhật tên
                if (requestEditProfileCustomer.getName() != null && !requestEditProfileCustomer.getName().isEmpty()) {
                    oldAccount.setName(requestEditProfileCustomer.getName());
                }

                // Xử lý đổi mật khẩu
                String oldPassword = requestEditProfileCustomer.getOldPassword();
                String newPassword = requestEditProfileCustomer.getNewPassword();

                if (StringUtils.hasText(newPassword) || StringUtils.hasText(oldPassword)) {
                    if (!StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
                        throw new UpdatedException("Cần nhập cả mật khẩu cũ và mật khẩu mới để đổi mật khẩu.");
                    }

                    if (!passwordEncoder.matches(oldPassword, oldAccount.getPassword())) {
                        throw new UpdatedException("Mật khẩu cũ không chính xác.");
                    }

                    // Mã hóa mật khẩu mới
                    String encodedNewPassword = passwordEncoder.encode(newPassword);
                    oldAccount.setPassword(encodedNewPassword);
                }

                // Lưu cập nhật vào cơ sở dữ liệu
                AccountForCustomer updatedAccount = accountForCustomerRepository.save(oldAccount);
                return modelMapper.map(updatedAccount, EditProfileCustomerResponse.class);
            } catch (DataIntegrityViolationException e) {
                if(e.getMessage().contains(requestEditProfileCustomer.getEmail())){
                    throw new UpdatedException("duplicate email!");
                } else if (e.getMessage().contains(requestEditProfileCustomer.getNewPassword())) {
                    throw new UpdatedException("duplicate password!");
                }
            }
        }
        return null;
    }



    // logic update profile cho employee


    public EditProfileEmployeeResponse updatedAccount(RequestEditProfileEmployee requestEditProfileEmployee, String id) {
//        AccountForEmployee account = modelMapper.map(requestEditProfileEmployee, AccountForEmployee.class);
            AccountForEmployee oldAccount = employeeRepository.findAccountForEmployeeByEmployeeId(id);
            if (oldAccount == null) {
                throw new Duplicate("Account not found!");// cho dung luon
            } else {
                try{
//                    if (account.getEmail() != null && !account.getEmail().isEmpty()) {
//                        oldAccount.setEmail(account.getEmail());
//                    }
//                    // Kiểm tra số điện thoại hợp lệ (ví dụ: 10 chữ số)
//                    if (account.getPhoneNumber() != null && !account.getPhoneNumber().isEmpty()) {
//                        oldAccount.setPhoneNumber(account.getPhoneNumber());
//                    }
//                    // Kiểm tra mật khẩu phải lớn hơn 6 ký tự
////                    String originPassword = account.getPassword();
////                    if (account.getPassword() != null && !account.getPassword().isEmpty()) {
////                        oldAccount.setPassword(passwordEncoder.encode(originPassword));
////                    }
//                    // Kiểm tra và cập nhật tên
//                    if (account.getName() != null && !account.getName().isEmpty()) {
//                        oldAccount.setName(account.getName());
//                    }
//
//                    // Kiểm tra và cập nhật ảnh
//                    if (account.getImg() != null && !account.getImg().isEmpty()) {
//                        oldAccount.setImg(account.getImg());
//                    }
//
//                    // Xử lý đổi mật khẩu
//                    String oldPassword = requestEditProfileEmployee.getOldPassword();
//                    String newPassword = requestEditProfileEmployee.getNewPassword();
//
//                    if (StringUtils.hasText(newPassword) || StringUtils.hasText(oldPassword)) {
//                        // Nếu muốn đổi mật khẩu, cả hai trường phải được điền
//                        if (!StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
//                            throw new UpdatedException("Cần nhập cả mật khẩu cũ và mật khẩu mới để đổi mật khẩu.");
//                        }
//
//                        // Kiểm tra mật khẩu cũ
//                        if (!passwordEncoder.matches(oldPassword, oldAccount.getPassword())) {
//                            throw new UpdatedException("Mật khẩu cũ không chính xác.");
//                        }
//
//                        // Kiểm tra độ dài mật khẩu mới (đã được kiểm tra qua annotation @Size)
//                        // Mã hóa mật khẩu mới và cập nhật
//                        String encodedNewPassword = passwordEncoder.encode(newPassword);
//                        oldAccount.setPassword(encodedNewPassword);
//                    }
//
//                    // Lưu cập nhật vào cơ sở dữ liệu
//                    AccountForEmployee updatedAccount = employeeRepository.save(oldAccount);
//                    return modelMapper.map(updatedAccount, EditProfileEmployeeResponse.class);



                    // phai lam nhu thu cong ntn vi modelMapper ko nhan biet dc new vs old password
                    // Cập nhật email
                    if (requestEditProfileEmployee.getEmail() != null && !requestEditProfileEmployee.getEmail().isEmpty()) {
                        oldAccount.setEmail(requestEditProfileEmployee.getEmail());
                    }

                    // Kiểm tra số điện thoại hợp lệ (ví dụ: 10 chữ số)
                    if (requestEditProfileEmployee.getPhoneNumber() != null && !requestEditProfileEmployee.getPhoneNumber().isEmpty()) {
                        oldAccount.setPhoneNumber(requestEditProfileEmployee.getPhoneNumber());
                    }

                    // Kiểm tra và cập nhật tên
                    if (requestEditProfileEmployee.getName() != null && !requestEditProfileEmployee.getName().isEmpty()) {
                        oldAccount.setName(requestEditProfileEmployee.getName());
                    }

                    // Kiểm tra và cập nhật ảnh
                    if (requestEditProfileEmployee.getImg() != null && !requestEditProfileEmployee.getImg().isEmpty()) {
                        oldAccount.setImg(requestEditProfileEmployee.getImg());
                    }

                    // Xử lý đổi mật khẩu
                    String oldPassword = requestEditProfileEmployee.getOldPassword();
                    String newPassword = requestEditProfileEmployee.getNewPassword();

                    if (StringUtils.hasText(newPassword) || StringUtils.hasText(oldPassword)) {
                        // Nếu muốn đổi mật khẩu, cả hai trường phải được điền
                        if (!StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
                            throw new UpdatedException("Cần nhập cả mật khẩu cũ và mật khẩu mới để đổi mật khẩu.");
                        }

                        // Kiểm tra mật khẩu cũ
                        if (!passwordEncoder.matches(oldPassword, oldAccount.getPassword())) {
                            throw new UpdatedException("Mật khẩu cũ không chính xác.");
                        }

                        // Mã hóa mật khẩu mới và cập nhật
                        String encodedNewPassword = passwordEncoder.encode(newPassword);
                        oldAccount.setPassword(encodedNewPassword);
                    }

                    // Lưu cập nhật vào cơ sở dữ liệu
                    AccountForEmployee updatedAccount = employeeRepository.save(oldAccount);
                    return modelMapper.map(updatedAccount, EditProfileEmployeeResponse.class);
                } catch (Exception e) {
                    if(e.getMessage().contains(requestEditProfileEmployee.getEmail())){
                        throw new UpdatedException("duplicate email!");
                    } else if (e.getMessage().contains(requestEditProfileEmployee.getPhoneNumber())) {
                        throw new UpdatedException("duplicate phone!");
                    } else if (e.getMessage().contains(requestEditProfileEmployee.getNewPassword())) {
                        throw new UpdatedException("duplicate password!");
                    }
                }
            }
            return null;
    }



    public EditProfileEmployeeResponse updatedAccountByManager(RequestUpdateProfileEmployeeByManager requestUpdateProfileEmployeeByManager, String id) {
        AccountForEmployee account = modelMapper.map(requestUpdateProfileEmployeeByManager, AccountForEmployee.class);
        AccountForEmployee oldAccount = employeeRepository.findAccountForEmployeeByEmployeeId(id);
        if (oldAccount == null || !oldAccount.getRole().equalsIgnoreCase("Stylist")) {
            throw new Duplicate("Account not found!");// cho dung luon
        } else {
            try{
                if (account.getStylistLevel() != null && !account.getStylistLevel().isEmpty()) {
                    oldAccount.setStylistLevel(account.getStylistLevel());
                }

                if (account.getKPI() != null) {
                    if(account.getKPI() < 0 ){
                        throw new UpdatedException("KPI must be at least 0");
                    }
                    oldAccount.setKPI(account.getKPI());
                }

                if(account.getStylistSelectionFee() != null){
                    if(account.getStylistSelectionFee() < 0){
                        throw new UpdatedException("Stylist Selection Fee must be at least 0");
                    }
                    oldAccount.setStylistSelectionFee(account.getStylistSelectionFee());
                }



                // Lưu cập nhật vào cơ sở dữ liệu
                AccountForEmployee updatedAccount = employeeRepository.save(oldAccount);
                return modelMapper.map(updatedAccount, EditProfileEmployeeResponse.class);
            } catch (Exception e) {
                throw new UpdatedException("employee can not update!");
            }
        }
    }

//    public EditSalaryEmployeeResponse updatedSalaryEmployee(RequestEditSsalaryEmployee requestEditSsalaryEmployee, String id) {
//        AccountForEmployee account = modelMapper.map(requestEditSsalaryEmployee, AccountForEmployee.class);
//        AccountForEmployee oldAccount = employeeRepository.findAccountForEmployeeByEmployeeId(id);
//        if (oldAccount == null) {
//            throw new Duplicate("Account not found!");// cho dung luon
//        } else {
//            try{
//                if (account.getBasicSalary() != null) {
//                    if(account.getBasicSalary() < 0 ){
//                        throw new UpdatedException("Basic Salary must be at least 0");
//                    }
//                    oldAccount.setBasicSalary(account.getBasicSalary());
//                }
//
//                if (account.getCommessionOverratedFromKPI() != null) {
//                    if(account.getCommessionOverratedFromKPI() < 0 ){
//                        throw new UpdatedException("Commession Overrated From KPI must be at least 0");
//                    }
//                    oldAccount.setCommessionOverratedFromKPI(account.getCommessionOverratedFromKPI());
//                }
//
//                if(account.getFineUnderatedFromKPI() != null){
//                    if(account.getFineUnderatedFromKPI() < 0){
//                        throw new UpdatedException("Fine Underated From KPI must be at least 0");
//                    }
//                    oldAccount.setFineUnderatedFromKPI(account.getFineUnderatedFromKPI());
//                }
//
//
//
//                // Lưu cập nhật vào cơ sở dữ liệu
//                AccountForEmployee updatedAccount = employeeRepository.save(oldAccount);
//                return modelMapper.map(updatedAccount, EditSalaryEmployeeResponse.class);
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//                throw new UpdatedException("employee can not update!");
//            }
//        }
//    }

    public EditSalaryEmployeeResponse updatedSalaryEmployee(RequestEditSsalaryEmployee requestEditSsalaryEmployee, String id) {
        AccountForEmployee oldAccount = employeeRepository.findAccountForEmployeeByEmployeeId(id);
        if (oldAccount == null) {
            throw new Duplicate("Account not found!");
        } else {
            try{
                String role = oldAccount.getRole();

                // Ánh xạ thủ công cho các thuộc tính có vấn đề
                if (requestEditSsalaryEmployee.getBasicSalary() != null) {
                    if(requestEditSsalaryEmployee.getBasicSalary() < 0){
                        throw new UpdatedException("Basic Salary must be at least 0");
                    }
                    oldAccount.setBasicSalary(requestEditSsalaryEmployee.getBasicSalary());
                }

                if("Stylist".equalsIgnoreCase(role)){
                    if (requestEditSsalaryEmployee.getCommessionOverratedFromKPI() != null) {
                        if(requestEditSsalaryEmployee.getCommessionOverratedFromKPI() < 0){
                            throw new UpdatedException("Commession Overrated From KPI must be at least 0");
                        }
                        oldAccount.setCommessionOverratedFromKPI(requestEditSsalaryEmployee.getCommessionOverratedFromKPI());
                    }

                    if(requestEditSsalaryEmployee.getFineUnderatedFromKPI() != null){
                        if(requestEditSsalaryEmployee.getFineUnderatedFromKPI() < 0){
                            throw new UpdatedException("Fine Underated From KPI must be at least 0");
                        }
                        oldAccount.setFineUnderatedFromKPI(requestEditSsalaryEmployee.getFineUnderatedFromKPI());
                    }
                }else{
                    oldAccount.setCommessionOverratedFromKPI(null);
                    oldAccount.setFineUnderatedFromKPI(null);
                }


                // Lưu cập nhật vào cơ sở dữ liệu
                AccountForEmployee updatedAccount = employeeRepository.save(oldAccount);
                return modelMapper.map(updatedAccount, EditSalaryEmployeeResponse.class);
            } catch (Exception e) {
                System.out.println(e.getMessage());
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
                throw new AccountBlockedException("Your account is blocked!");
            } else {
                AccountForEmployeeResponse accountResponseForEmployee = modelMapper.map(account, AccountForEmployeeResponse.class);
                accountResponseForEmployee.setToken(tokenService.generateTokenEmployee(account));
                return accountResponseForEmployee;
            }
        } catch (BadCredentialsException e) { //lỗi này xuất hiện khi xác thực thất bại
            throw new AccountNotFoundException("Username or password invalid!");
        }

    }

//    //LOGIN EMPLOYEE
//    public AccountForEmployeeResponse loginForEmployee(LoginRequestForEmployee loginRequestForEmployee){
//        try{
//            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
//                    loginRequestForEmployee.getUsername(),
//                    loginRequestForEmployee.getPassword()
//            ));
//
//            //=> tài khoản có tồn tại
//            AccountForEmployee account = (AccountForEmployee) authentication.getPrincipal();
//            if(account.isDeleted()){
//                throw new AccountBlockedException("Your account is blocked!");
//            } else {
//                AccountForEmployeeResponse accountResponseForEmployee = modelMapper.map(account, AccountForEmployeeResponse.class);
//                accountResponseForEmployee.setToken(tokenService.generateTokenEmployee(account));
//                return accountResponseForEmployee;
//            }
//        } catch (BadCredentialsException e) { //lỗi này xuất hiện khi xác thực thất bại
//            throw new AccountNotFoundException("Username or password invalid!");
//        }
//
//    }

    //delete Acc customer
    public AccountForCustomerResponse deleteAccountForCustomer(String phoneNumber){
        // tim toi id ma FE cung cap
        AccountForCustomer accountForCustomerNeedDelete = accountForCustomerRepository.findByPhoneNumber(phoneNumber);
        if(accountForCustomerNeedDelete == null){
            throw new Duplicate("Account For Customer not found!"); // dung tai day
        }

        accountForCustomerNeedDelete.setDeleted(true);
        AccountForCustomer deletedAccountForCustomer = accountForCustomerRepository.save(accountForCustomerNeedDelete);
        return modelMapper.map(deletedAccountForCustomer, AccountForCustomerResponse.class);
    }

    //delete Acc employee
    public AccountForEmployeeResponse deleteAccountForEmployee(String emlpoyeeId){
        // tim toi id ma FE cung cap
        AccountForEmployee accountForEmployeeNeedDelete = employeeRepository.findAccountForEmployeeByEmployeeId(emlpoyeeId);
        if(accountForEmployeeNeedDelete == null){
            throw new Duplicate("Account For Customer not found!"); // dung tai day
        }

        accountForEmployeeNeedDelete.setDeleted(true);
        AccountForEmployee deletedAccountForEmployee = employeeRepository.save(accountForEmployeeNeedDelete);
        return modelMapper.map(deletedAccountForEmployee, AccountForEmployeeResponse.class);
    }

    // show list of Account For Customer
    public List<AccountForCustomer> getAllAccountForCustomer(){
        List<AccountForCustomer> accountForCustomers = accountForCustomerRepository.findAccountForCustomersByIsDeletedFalse();
        return accountForCustomers;
    }

    // show list of Account For Employee
    public List<AccountForEmployee> getAllAccountForEmployee(){
        List<AccountForEmployee> accountForEmployees = employeeRepository.findAccountForEmployeesByIsDeletedFalse();
        return accountForEmployees;
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

    public UserDetails loadUserByName(String username) throws UsernameNotFoundException {
        if(employeeRepository.findAccountForEmployeeByUsername(username)!=null){
            return employeeRepository.findAccountForEmployeeByUsername(username);
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
//            throw new ClassCastException("Current user is not a valid customer.");
            return null;
        }
    }

    public AccountForEmployee getCurrentAccountForEmployee() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Kiểm tra nếu principal là kiểu AccountForEmployee
        if (principal instanceof AccountForEmployee) {
            AccountForEmployee account = (AccountForEmployee) principal;
            return employeeRepository.findAccountForEmployeeByEmployeeId(account.getEmployeeId());
        } else {
            return null;
//            throw new ClassCastException("Current user is not an employee.");
        }
    }

//    public AccountForEmployee getCurrentAccountForEmployee() {
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        // Kiểm tra nếu principal là kiểu UserDetails và lấy thông tin
//        if (principal instanceof UserDetails) {
//            UserDetails userDetails = (UserDetails) principal;
//            // Trích xuất thông tin từ UserDetails (username, employee ID, etc.)
//            String employeeId = userDetails.getUsername();  // hoặc dùng getEmployeeId nếu có
//            return employeeRepository.findAccountForEmployeeByEmployeeId(employeeId);
//        } else {
//            throw new ClassCastException("Current user is not a valid employee.");
//        }
//    }

}