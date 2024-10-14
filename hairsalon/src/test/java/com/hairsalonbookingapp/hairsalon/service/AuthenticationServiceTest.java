package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.AccountForCustomer;
import com.hairsalonbookingapp.hairsalon.exception.AccountBlockedException;
import com.hairsalonbookingapp.hairsalon.exception.AccountNotFoundException;
import com.hairsalonbookingapp.hairsalon.model.AccountResponseForCustomer;
import com.hairsalonbookingapp.hairsalon.model.LoginRequestForCustomer;
import com.hairsalonbookingapp.hairsalon.repository.CustomerRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.testng.Assert.*;

@SpringBootTest
public class AuthenticationServiceTest {

    @InjectMocks
    AuthenticationService authenticationService;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    ModelMapper modelMapper;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    CustomerRepository customerRepository;

    @Mock
    TokenService tokenService;

    @Test
    public void testIsPhoneNumber() {
        boolean result = authenticationService.isPhoneNumber("0388357295");
        Assert.assertTrue(result, "ERROR");
    }

    @BeforeClass
    public void init() {
        MockitoAnnotations.openMocks(this); // Khởi tạo các mock
    }


    @Test
    public void testLoginForCustomer_Success() {
        String phonenumber = "0328337294";
        String password = "String1";
        String token = "asdjfjfjfsfs";

        LoginRequestForCustomer loginRequestForCustomer = new LoginRequestForCustomer();
        loginRequestForCustomer.setPhoneNumber(phonenumber);
        loginRequestForCustomer.setPassword(password);

        // Mock AccountForCustomer
        AccountForCustomer mockAccount = new AccountForCustomer();
        mockAccount.setCustomerName("Anh");
        mockAccount.setEmail("phuc@gmail.com");
        mockAccount.setPoint(0);
        mockAccount.setPhoneNumber(phonenumber);
        mockAccount.setDeleted(false);

        // Mock AccountResponseForCustomer
        AccountResponseForCustomer accountResponseForCustomer = new AccountResponseForCustomer();
        accountResponseForCustomer.setCustomerName("Anh");
        accountResponseForCustomer.setEmail("phuc@gmail.com");
        accountResponseForCustomer.setToken(token); // Sử dụng token đã giả lập

        // Mock Authentication object
        Authentication mockAuthentication = Mockito.mock(Authentication.class);
        Mockito.when(mockAuthentication.getPrincipal()).thenReturn(mockAccount);

        // Giả lập repository và các service
        Mockito.when(customerRepository.findAccountForCustomerByPhoneNumber(phonenumber)).thenReturn(mockAccount);
        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);

        Mockito.when(modelMapper.map(mockAccount, AccountResponseForCustomer.class)).thenReturn(accountResponseForCustomer);

        Mockito.when(tokenService.generateTokenCustomer(mockAccount)).thenReturn(token);

        // Gọi phương thức kiểm thử
        AccountResponseForCustomer responseBody = authenticationService.loginForCustomer(loginRequestForCustomer);

        // Kiểm tra thông tin trả về
        Assert.assertEquals(responseBody.getCustomerName(), "Anh");
        Assert.assertEquals(responseBody.getEmail(), "phuc@gmail.com");
        Assert.assertEquals(responseBody.getToken(), token); // Kiểm tra token
    }


    @Test(expectedExceptions = AccountNotFoundException.class)
    public void testLoginForCustomer_Fail_WrongPassword() {
        String phonenumber = "0327344923";
        String password = "String";

        LoginRequestForCustomer loginRequestForCustomer = new LoginRequestForCustomer();
        loginRequestForCustomer.setPhoneNumber(phonenumber);
        loginRequestForCustomer.setPassword(password);

        // Mock AccountForCustomer
        AccountForCustomer mockAccount = new AccountForCustomer();
        mockAccount.setCustomerName("Anh");
        mockAccount.setEmail("phuc@gmail.com");
        mockAccount.setPoint(0);
        mockAccount.setPhoneNumber(phonenumber);
        mockAccount.setDeleted(false);


        // Giả lập repository và các service
        //Mockito.when(customerRepository.findAccountForCustomerByPhoneNumber(phonenumber)).thenReturn(mockAccount);
        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AccountNotFoundException("Phonenumber or password invalid!"));

        // Gọi phương thức kiểm thử
        authenticationService.loginForCustomer(loginRequestForCustomer);

        // Kiểm tra thông tin trả về
        //Assert.assertEquals(response.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }


    @Test(expectedExceptions = AccountBlockedException.class)
    public void testLoginForCustomer_AccountBlocked() {
        // Tạo dữ liệu giả lập
        LoginRequestForCustomer loginRequest = new LoginRequestForCustomer();
        loginRequest.setPhoneNumber("123456789");
        loginRequest.setPassword("password");

        AccountForCustomer account = new AccountForCustomer();
        account.setPhoneNumber("123456789");
        account.setPassword("password");
        account.setDeleted(true); // Account bị khoá

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(account);
        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        // Gọi phương thức và mong đợi AccountBlockedException
        authenticationService.loginForCustomer(loginRequest);
    }

}