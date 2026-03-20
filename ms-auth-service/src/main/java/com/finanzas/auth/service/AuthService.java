package com.finanzas.auth.service;

import com.finanzas.auth.dto.LoginRequest;
import com.finanzas.auth.dto.LoginResponse;
import com.finanzas.auth.entity.User;
import com.finanzas.auth.repository.UserRepository;
import com.finanzas.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        // Buscar usuario en la base de datos
        User user = userRepository.findByUsername(username).orElse(null);
        
        if (user != null && user.getPassword().equals(password)) {
            String token = jwtUtil.generateToken(username);
            return new LoginResponse("Login successful", true, username, token);
        } else {
            return new LoginResponse("Invalid credentials", false, null, null);
        }
    }
}
