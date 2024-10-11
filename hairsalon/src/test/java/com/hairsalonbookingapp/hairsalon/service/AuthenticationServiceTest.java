package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.model.AccountResponseForCustomer;
import com.hairsalonbookingapp.hairsalon.model.LoginRequestForCustomer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class AuthenticationServiceTest {

    AuthenticationService authenticationService = new AuthenticationService();

    @Autowired
    AuthenticationManager authenticationManager;

    @Test
    public void testIsPhoneNumber() {
        boolean result = authenticationService.isPhoneNumber("0388357295");
        Assert.assertTrue(result, "ERROR");
    }

    @Test
    public void testLoginForCustomer() {
        String phonenumber = "0328337294";
        String password = "String1";

        LoginRequestForCustomer loginRequestForCustomer = new LoginRequestForCustomer();
        loginRequestForCustomer.setPhoneNumber(phonenumber);
        loginRequestForCustomer.setPassword(password);

        AccountResponseForCustomer account = authenticationService.loginForCustomer(loginRequestForCustomer);
        ResponseEntity response = ResponseEntity.ok(account);

        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK); // KIỂM THỬ STATUS

        AccountResponseForCustomer responseBody = (AccountResponseForCustomer) response.getBody();

        Assert.assertEquals(responseBody.getPhoneNumber(), "0328337294");

        Assert.assertEquals(responseBody.getCustomerName(), "Cong");

        Assert.assertEquals(responseBody.getEmail(), "cong@gmail.com");
    }
}