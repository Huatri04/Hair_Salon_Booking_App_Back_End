package com.hairsalonbookingapp.hairsalon.config;

import com.hairsalonbookingapp.hairsalon.entity.AccountForCustomer;
import com.hairsalonbookingapp.hairsalon.entity.AccountForEmployee;
import com.hairsalonbookingapp.hairsalon.entity.SoftwareSupportApplication;
import com.hairsalonbookingapp.hairsalon.model.RequestEditProfileCustomer;
import com.hairsalonbookingapp.hairsalon.model.RequestEditProfileEmployee;
import com.hairsalonbookingapp.hairsalon.model.RequestSoftwareSupportApplication;
import com.hairsalonbookingapp.hairsalon.service.AuthenticationService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    Filter filter;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)  throws Exception {
//        return http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(
//                        req -> req
//                                .requestMatchers("/**")
//                                .permitAll()
//                                .anyRequest()
//                                .authenticated()
//
//                )
//                .userDetailsService(authenticationService)
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class).build();
        return http
                .cors().and()
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll() // Cho phép tất cả pre-flight requests
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated()
                )
                .userDetailsService(authenticationService)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
//    @Bean
//    public ModelMapper modelMapper() {
//        ModelMapper modelMapper = new ModelMapper();
//
//        // Tạo TypeMap cho RequestEditProfileCustomer -> AccountForCustomer
//        TypeMap<RequestEditProfileCustomer, AccountForCustomer> typeMap =
//                modelMapper.createTypeMap(RequestEditProfileCustomer.class, AccountForCustomer.class);
//
//        // Bỏ qua việc ánh xạ trường password
//        typeMap.addMappings(mapper -> {
//            mapper.skip(AccountForCustomer::setPassword);
//        });
//
//        // Tạo TypeMap cho RequestEditProfileEmployee -> AccountForEmployee
//        TypeMap<RequestEditProfileEmployee, AccountForEmployee> employeeTypeMap =
//                modelMapper.createTypeMap(RequestEditProfileEmployee.class, AccountForEmployee.class);
//
//        // Bỏ qua việc ánh xạ trường password cho Employee
//        employeeTypeMap.addMappings(mapper -> {
//            mapper.skip(AccountForEmployee::setPassword);
//        });
//
//        return modelMapper;
//    }
//@Bean
//public ModelMapper modelMapper() {
//    ModelMapper modelMapper = new ModelMapper();
//
//    // Đặt Matching Strategy để tăng độ chính xác
//    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//
//    // Bỏ qua ánh xạ các trường liên kết để thiết lập thủ công
//    modelMapper.typeMap(RequestSoftwareSupportApplication.class, SoftwareSupportApplication.class).addMappings(mapper -> {
//        mapper.skip(SoftwareSupportApplication::setCustomer);
//        mapper.skip(SoftwareSupportApplication::setEmployee);
//    });
//
//    return modelMapper;
//}

}
