package com.hairsalonbookingapp.hairsalon.APITest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hairsalonbookingapp.hairsalon.model.request.LoginRequestForCustomer;
import com.hairsalonbookingapp.hairsalon.model.request.LoginRequestForEmployee;
import com.hairsalonbookingapp.hairsalon.model.request.RegisterRequestForCustomer;
import com.hairsalonbookingapp.hairsalon.model.request.RegisterRequestForEmloyee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AuthenticationAPITest {
    @SpringBootTest
    @AutoConfigureMockMvc
    @Transactional
    public class AuthControllerTest extends AbstractTestNGSpringContextTests {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        // Setup method if needed
        @BeforeMethod
        public void setUp() {
            // Initialize test data or configurations
        }

        // Teardown method if needed
        @AfterMethod
        public void tearDown() {
            // Clean up after tests
        }

        // Test Customer Registration - Success
        @Test
        public void testRegisterCustomer_Success() throws Exception {
            RegisterRequestForCustomer registerRequest = new RegisterRequestForCustomer();
            registerRequest.setPhoneNumber("0799128951");
            registerRequest.setEmail("customer1@example.com");
            registerRequest.setPassword("Password123");

            mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("customer1"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("customer1@example.com"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.token").isNotEmpty());
        }

        // Test Employee Registration - Success
        @Test
        public void testRegisterEmployee_Success() throws Exception {
            RegisterRequestForEmloyee registerRequest = new RegisterRequestForEmloyee();
            registerRequest.setUsername("employee1");
            registerRequest.setEmail("employee1@example.com");
            registerRequest.setPassword("Password123");
            // Set additional fields if any

            mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/registerEmployee")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("employee1"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("employee1@example.com"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.role").isNotEmpty())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.token").isNotEmpty());
        }

        // Test Customer Registration - Existing Username
        @Test
        public void testRegisterCustomer_ExistingUsername() throws Exception {
            RegisterRequestForCustomer registerRequest = new RegisterRequestForCustomer();
            registerRequest.setPhoneNumber("0799128951");
            registerRequest.setEmail("customer2@example.com");
            registerRequest.setPassword("Password123");

            // First registration should succeed
            mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(MockMvcResultMatchers.status().isOk());

            // Second registration with the same username should fail
            mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
            // Optionally, verify error message
            //.andExpect(jsonPath("$.message").value("Username is already taken"));
        }

        // Test Employee Registration - Existing Email
        @Test
        public void testRegisterEmployee_ExistingEmail() throws Exception {
            RegisterRequestForEmloyee registerRequest = new RegisterRequestForEmloyee();
            registerRequest.setUsername("employee2");
            registerRequest.setEmail("employee2@example.com");
            registerRequest.setPassword("Password123");
            // Set additional fields if any

            // First registration should succeed
            mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/registerEmployee")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(MockMvcResultMatchers.status().isOk());

            // Second registration with the same email should fail
            RegisterRequestForEmloyee duplicateEmailRequest = new RegisterRequestForEmloyee();
            duplicateEmailRequest.setUsername("employee3");
            duplicateEmailRequest.setEmail("employee2@example.com"); // Same email
            duplicateEmailRequest.setPassword("Password123");
            // Set additional fields if any

            mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/registerEmployee")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(duplicateEmailRequest)))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
            // Optionally, verify error message
            //.andExpect(jsonPath("$.message").value("Email is already in use"));
        }

        // Test Customer Login - Success
        @Test
        public void testLoginCustomer_Success() throws Exception {
            // First, register a customer
            RegisterRequestForCustomer registerRequest = new RegisterRequestForCustomer();
            registerRequest.setPhoneNumber("0799128952");
            registerRequest.setEmail("customerLogin@example.com");
            registerRequest.setPassword("Password123");

            mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(MockMvcResultMatchers.status().isOk());

            // Attempt to login
            LoginRequestForCustomer loginRequest = new LoginRequestForCustomer();
            loginRequest.setPhoneNumber("0799128952");
            loginRequest.setPassword("Password123");

            mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/loginCustomer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("customerLogin"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("customerLogin@example.com"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.token").isNotEmpty());
        }

        // Test Employee Login - Success
        @Test
        public void testLoginEmployee_Success() throws Exception {
            // First, register an employee
            RegisterRequestForEmloyee registerRequest = new RegisterRequestForEmloyee();
            registerRequest.setUsername("employeeLogin");
            registerRequest.setEmail("employeeLogin@example.com");
            registerRequest.setPassword("Password123");
            // Set additional fields if any

            mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/registerEmployee")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(MockMvcResultMatchers.status().isOk());

            // Attempt to login
            LoginRequestForEmployee loginRequest = new LoginRequestForEmployee();
            loginRequest.setUsername("employeeLogin");
            loginRequest.setPassword("Password123");

            mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/loginEmployee")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("employeeLogin"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("employeeLogin@example.com"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.role").isNotEmpty())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.token").isNotEmpty());
        }

        // Test Customer Login - Invalid Credentials
        @Test
        public void testLoginCustomer_InvalidCredentials() throws Exception {
            LoginRequestForCustomer loginRequest = new LoginRequestForCustomer();
            loginRequest.setPhoneNumber("0799128956");
            loginRequest.setPassword("WrongPassword");

            mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/loginCustomer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized());
            // Optionally, verify error message
            //.andExpect(jsonPath("$.message").value("Invalid username or password"));
        }

        // Test Employee Login - Invalid Credentials
        @Test
        public void testLoginEmployee_InvalidCredentials() throws Exception {
            LoginRequestForEmployee loginRequest = new LoginRequestForEmployee();
            loginRequest.setUsername("nonexistentEmployee");
            loginRequest.setPassword("WrongPassword");

            mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/loginEmployee")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized());
            // Optionally, verify error message
            //.andExpect(jsonPath("$.message").value("Invalid username or password"));
        }
    }
}
