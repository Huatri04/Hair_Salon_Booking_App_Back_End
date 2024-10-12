package com.hairsalonbookingapp.hairsalon.api;

import com.hairsalonbookingapp.hairsalon.entity.AccountForCustomer;
import com.hairsalonbookingapp.hairsalon.entity.AccountForEmployee;
import com.hairsalonbookingapp.hairsalon.exception.Duplicate;
import com.hairsalonbookingapp.hairsalon.model.request.RegisterRequestForCustomer;
import com.hairsalonbookingapp.hairsalon.model.request.RegisterRequestForEmloyee;
import com.hairsalonbookingapp.hairsalon.model.response.AccountForCustomerResponse;
import com.hairsalonbookingapp.hairsalon.model.response.AccountForEmployeeResponse;
import com.hairsalonbookingapp.hairsalon.repository.AccountForCustomerRepository;
import com.hairsalonbookingapp.hairsalon.repository.EmployeeRepository;
import com.hairsalonbookingapp.hairsalon.service.AuthenticationService;
import org.hamcrest.CoreMatchers;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class AuthenticationAPITest {

    @Mock
    private AccountForCustomerRepository accountForCustomerRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper; // Thêm mock cho ModelMapper

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoginForCustomer() {
    }

    @Test
    public void testLoginForEmployee() {
    }

    @Test
    public void testRegisterForCustomerSuccess() {
        // Tạo đối tượng AccountForCustomer với dữ liệu mẫu
        AccountForCustomer accountForCustomer = new AccountForCustomer();
        accountForCustomer.setPhoneNumber("0799128954");
        accountForCustomer.setEmail("string@gmail.com");
        accountForCustomer.setName("anh");
        accountForCustomer.setPassword("123456");

        // Tạo đối tượng RegisterRequestForCustomer từ dữ liệu mẫu
        RegisterRequestForCustomer registerRequestForCustomer = new RegisterRequestForCustomer();
        registerRequestForCustomer.setPhoneNumber(accountForCustomer.getPhoneNumber());
        registerRequestForCustomer.setEmail(accountForCustomer.getEmail());
        registerRequestForCustomer.setName(accountForCustomer.getName());
        registerRequestForCustomer.setPassword(accountForCustomer.getPassword());

        // Mô phỏng hành vi của modelMapper khi chuyển đổi DTO sang Entity
        Mockito.when(modelMapper.map(ArgumentMatchers.eq(registerRequestForCustomer), ArgumentMatchers.eq(AccountForCustomer.class))).thenReturn(accountForCustomer);

        // Mô phỏng hành vi của repository khi lưu đối tượng
        Mockito.when(accountForCustomerRepository.save(ArgumentMatchers.any(AccountForCustomer.class))).thenReturn(accountForCustomer);

        // Tạo đối tượng AccountForCustomerResponse giả định
        AccountForCustomerResponse response = new AccountForCustomerResponse();
        response.setPhoneNumber(accountForCustomer.getPhoneNumber());
        response.setEmail(accountForCustomer.getEmail());
        response.setName(accountForCustomer.getName());
        // Giả sử bạn có thêm các trường khác trong response

        // Mô phỏng hành vi của modelMapper khi chuyển đổi Entity sang Response DTO
        Mockito.when(modelMapper.map(ArgumentMatchers.eq(accountForCustomer), ArgumentMatchers.eq(AccountForCustomerResponse.class))).thenReturn(response);

        // Gọi phương thức register trong AuthenticationService
        AccountForCustomerResponse registeredAccountForCustomer = authenticationService.register(registerRequestForCustomer);

    }

    @Test(expectedExceptions = Duplicate.class)
    public void testRegisterPhoneNumberAlreadyExists() {
        // Tạo đối tượng AccountForCustomer với dữ liệu mẫu (đã tồn tại)
        AccountForCustomer existingAccount = new AccountForCustomer();
        existingAccount.setPhoneNumber("0799128953"); // Số điện thoại trùng
        existingAccount.setEmail("existing@gmail.com");
        existingAccount.setName("existingUser");
        existingAccount.setPassword("existingPassword");

        // Tạo đối tượng RegisterRequestForCustomer từ dữ liệu mẫu
        RegisterRequestForCustomer registerRequestForCustomer = new RegisterRequestForCustomer();
        registerRequestForCustomer.setPhoneNumber("0799128953"); // Số điện thoại trùng
        registerRequestForCustomer.setEmail("newuser@gmail.com");
        registerRequestForCustomer.setName("newUser");
        registerRequestForCustomer.setPassword("newPassword");

        // Mô phỏng hành vi của modelMapper khi chuyển đổi DTO sang Entity
        AccountForCustomer mappedAccount = new AccountForCustomer();
        mappedAccount.setPhoneNumber("0799128953");
        mappedAccount.setEmail("newuser@gmail.com");
        mappedAccount.setName("newUser");
        mappedAccount.setPassword("newPassword");
        Mockito.when(modelMapper.map(ArgumentMatchers.eq(registerRequestForCustomer), ArgumentMatchers.eq(AccountForCustomer.class))).thenReturn(mappedAccount);

        // Mô phỏng hành vi của repository khi kiểm tra số điện thoại đã tồn tại
        Mockito.when(accountForCustomerRepository.existsByPhoneNumber("0799128953")).thenReturn(true);

        // Nếu phương thức sử dụng existsByEmail, mô phỏng nó nếu cần
        Mockito.when(accountForCustomerRepository.existsByEmail("newuser@gmail.com")).thenReturn(false);

        // Gọi phương thức register trong AuthenticationService và mong đợi ngoại lệ
        authenticationService.register(registerRequestForCustomer);
    }

    @Test
    public void testRegisterEmployee() {
        // Tạo đối tượng AccountForCustomer với dữ liệu mẫu
        AccountForEmployee accountForEmployee = new AccountForEmployee();
        accountForEmployee.setPhoneNumber("0799128954");
        accountForEmployee.setEmail("string@gmail.com");
        accountForEmployee.setName("anh");
        accountForEmployee.setPassword("123456");
        accountForEmployee.setRole("Stylist");
        accountForEmployee.setUsername("anh123");

        // Tạo đối tượng RegisterRequestForCustomer từ dữ liệu mẫu
        RegisterRequestForEmloyee registerRequestForEmloyee = new RegisterRequestForEmloyee();
        registerRequestForEmloyee.setPhoneNumber(accountForEmployee.getPhoneNumber());
        registerRequestForEmloyee.setEmail(accountForEmployee.getEmail());
        registerRequestForEmloyee.setName(accountForEmployee.getName());
        registerRequestForEmloyee.setPassword(accountForEmployee.getPassword());
        registerRequestForEmloyee.setRole(accountForEmployee.getRole());
        registerRequestForEmloyee.setUsername(accountForEmployee.getUsername());

        // Mô phỏng hành vi của modelMapper khi chuyển đổi DTO sang Entity
        Mockito.when(modelMapper.map(ArgumentMatchers.eq(registerRequestForEmloyee), ArgumentMatchers.eq(AccountForEmployee.class))).thenReturn(accountForEmployee);

        // Mô phỏng hành vi của passwordEncoder
        Mockito.when(passwordEncoder.encode(ArgumentMatchers.anyString())).thenReturn("encodedPassword");


        // Mô phỏng hành vi của repository khi lưu đối tượng
        Mockito.when(employeeRepository.save(ArgumentMatchers.any(AccountForEmployee.class))).thenReturn(accountForEmployee);


        // Tạo đối tượng AccountForCustomerResponse giả định
        AccountForEmployeeResponse response = new AccountForEmployeeResponse();
        response.setPhoneNumber(accountForEmployee.getPhoneNumber());
        response.setEmail(accountForEmployee.getEmail());
        response.setName(accountForEmployee.getName());
        response.setRole(accountForEmployee.getRole());
        response.setUsername(accountForEmployee.getUsername());
        // Giả sử bạn có thêm các trường khác trong response

        // Mô phỏng hành vi của modelMapper khi chuyển đổi Entity sang Response DTO
        Mockito.when(modelMapper.map(ArgumentMatchers.eq(accountForEmployee), ArgumentMatchers.eq(AccountForEmployeeResponse.class))).thenReturn(response);

        // Gọi phương thức register trong AuthenticationService
        AccountForEmployeeResponse registeredAccountForEmployee = authenticationService.register(registerRequestForEmloyee);
    }
}