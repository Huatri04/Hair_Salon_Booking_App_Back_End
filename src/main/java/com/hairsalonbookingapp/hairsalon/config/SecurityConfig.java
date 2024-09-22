package com.hairsalonbookingapp.hairsalon.config;

import com.hairsalonbookingapp.hairsalon.service.AuthenticationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    Filter filter;

    @Bean // de danh dau caai ham nay thanh thu vien khi dung chi can autowired
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)  throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        req -> req
                                .requestMatchers("/**")// cho phep thang nao dc truy cap vao he thong (/**) la cho tat ca
                                .permitAll()
                                .anyRequest()
                                .authenticated()

                )
                .userDetailsService(authenticationService) // cho spring security biet dau la lop service cua minh
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class).build(); // cho spring security biet dau la lop filter cua minh
    }
}
