package com.finanzas.auth.service;

import com.finanzas.auth.dto.LoginRequest;
import com.finanzas.auth.dto.LoginResponse;
import com.finanzas.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        if (isValidCredentials(username, password)) {
            String token = jwtUtil.generateToken(username);
            return new LoginResponse("Login successful", true, username, token);
        } else {
            return new LoginResponse("Invalid credentials", false, null, null);
        }
    }

    private boolean isValidCredentials(String username, String password) {
        return ("admin".equals(username) && "admin123".equals(password)) ||
               ("user".equals(username) && "user123".equals(password));
    }
}
