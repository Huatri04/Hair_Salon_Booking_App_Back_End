package com.hairsalonbookingapp.hairsalon;

import com.hairsalonbookingapp.hairsalon.api.AuthenticationAPI;
import com.hairsalonbookingapp.hairsalon.model.AccountResponseForCustomer;
import com.hairsalonbookingapp.hairsalon.model.AccountResponseForEmployee;
import com.hairsalonbookingapp.hairsalon.model.LoginRequestForCustomer;
import com.hairsalonbookingapp.hairsalon.service.AuthenticationService;
import com.hairsalonbookingapp.hairsalon.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertNotNull;

@SpringBootTest
public class LoginTester {

    AuthenticationService authenticationService = new AuthenticationService();

    @Test
    void contextLoads() {
        // Nếu không có lỗi xảy ra ở đây, có nghĩa là ngữ cảnh đã được load thành công.
        //assertNotNull(authenticationService, "AuthenticationService should not be null");
        try {
            assertNotNull(authenticationService, "AuthenticationService should not be null");
        } catch (Exception e) {
            e.printStackTrace(); // In ra thông báo lỗi chi tiết
        }
    }

    @Test
    public void testValidLoginCustomer(){
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
