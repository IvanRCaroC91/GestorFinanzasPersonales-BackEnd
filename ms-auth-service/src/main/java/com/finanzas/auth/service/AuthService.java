package com.finanzas.auth.service;

import com.finanzas.auth.dto.LoginRequest;
import com.finanzas.auth.dto.LoginResponse;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public LoginResponse login(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        if ("admin".equals(username) && "admin123".equals(password)) {
            return new LoginResponse("Login successful", true, username);
        } else if ("user".equals(username) && "user123".equals(password)) {
            return new LoginResponse("Login successful", true, username);
        } else {
            return new LoginResponse("Invalid credentials", false, null);
        }
    }
}
