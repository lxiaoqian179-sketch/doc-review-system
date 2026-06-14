package com.example.docreview.service;

import com.example.docreview.dto.LoginRequest;
import com.example.docreview.dto.LoginResponse;
import com.example.docreview.entity.User;
import com.example.docreview.repository.UserRepository;
import com.example.docreview.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public LoginResponse login(LoginRequest request) {
        // 1. 查資料庫
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        // 2. 比對密碼
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密碼錯誤");
        }

        // 3. 產生 token
        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole().name());
        return new LoginResponse(token, user.getUsername(), user.getRole().name());
    }
}